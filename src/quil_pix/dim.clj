(ns quil-pix.dim
  (:require [quil.core :as q]           
            [clojure.core.reducers :as r]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)]
            [taoensso.timbre.appenders.core :as appenders]
            [quil-pix.common :refer [ppmap xy xy->i random-color rgb draw setup]]))

(defn dim
  "average a location with its neighbors"
  [i pixels]
  (->> (get pixels i)
       (rgb)
       (map #(int (* % 0.9)))
       (apply q/color)))

(defn next-image
  [strat pixels]
  (try
    (profile :info strat
             (let [dim #(dim % pixels)
                   indexes (range (count pixels))]
               (condp = strat
                 :map     (mapv dim indexes)
                 :pmap    (into [] (pmap dim indexes))
                 :ppmap   (into [] (ppmap 1000 dim indexes))
                 :reducer (into [] (r/foldcat (r/map (bound-fn* dim) (vec indexes)))))))
    (catch Exception e (info "exception: " (.getMessage e)))))

(defonce pixels (atom nil))
(defonce graphics (atom nil))

(defn sketch
  [strat width]
  (q/sketch
   :title "Dim"
   :size [width width]
                                        ; setup function called only once, during sketch initialization.
   :setup (setup width)
                                        ; update-state is called on each iteration before draw-state.
                                        ; :update update-state
   :draw (draw #(next-image strat %))
   :features [:keep-on-top]))
