(ns quil-pix.blur
  (:require [quil.core :as q]           
            [clojure.core.reducers :as r]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)]
            [taoensso.timbre.appenders.core :as appenders]
            [quil-pix.common :refer [ppmap xy xy->i random-color rgb draw setup avg-colors]]))

(defn neighbors
  "Return a (rad x rad) square of a locations neighbors"
  [i rad width height]
  (let [[x1 y1] (xy i width)
        m  (* -1 rad)
        n  (inc rad)]
    (for [x2 (range m n)
          :let [x (+ x1 x2)]
          :when (and (> x -1) (< x width))
          
          y2 (range m n)
          :let [y (+ y1 y2)]
          :when (and (> y -1) (< y height))]
      [x y])))

(defn blur
  "average a location with its neighbors"
  [i rad width height pixels]
  (let [cells (neighbors i rad width height)
        cc    (count cells)]
    (->> cells
         (map #(get pixels (xy->i % width)))
         avg-colors)))

(defn next-image
  [strat pixels]
  (try
    (profile :info strat
             (let [blur #(blur % 3 (q/width) (q/height) pixels)
                   indexes (range (count pixels))]
               (condp = strat
                 :map     (mapv blur indexes)
                 :pmap    (into [] (pmap blur indexes))
                 :ppmap   (into [] (ppmap 1000 blur indexes))
                 :reducer (into [] (r/foldcat (r/map (bound-fn* blur) (vec indexes)))))))
    (catch Exception e (info "exception: " (.getMessage e)))))

(defn sketch
  [strat width]
  (q/sketch
   :title "It was you, you blur!"
   :size [width width]
                                        ; setup function called only once, during sketch initialization.
   :setup (setup width)
                                        ; update-state is called on each iteration before draw-state.
                                        ; :update update-state
   :draw (draw #(next-image strat %))
   :features [:keep-on-top]))
