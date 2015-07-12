(ns quil-pix.blur
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]
            [clojure.java.io :as io]
            [clojure.core.reducers :as r]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)]
            [taoensso.timbre.appenders.core :as appenders]))

(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "logs.txt"})}})

(def width 150)
(def height 150)
(def pixcount (* width height))

(defn xy
  "Given a linear location and the grid's width, return x and y"
  [i width]
  [(mod i width) (int (/ i width))])

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

(defn xy->i
  "return linear location given x and y"
  [[x y] width]
  (+ x (* y width)))

(defn random-color
  "Used for testing"
  []
  (q/color (q/random 255) (q/random 255) (q/random 255)))

(defn rgb
  [pixel]
  (map #(% pixel) [q/red q/green q/blue]))

(defn blur
  "average a location with its neighbors"
  [i rad row-len pixels]
  (let [cells (neighbors i rad width height)
        cc    (count cells)]
    (->> cells
         (map #(rgb (get pixels (xy->i % row-len))))
         (apply map +)
         (map #(int (/ % cc)))
         (apply q/color))))

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel
  overhead worthwhile"
  [grain-size f & colls]
  (apply concat
         (apply pmap
                (fn [& pgroups] (doall (apply map f pgroups)))
                (map (partial partition-all grain-size) colls))))

(defn next-image
  [strat pixels]
  (profile :info strat
           (let [blur #(blur % 3 width pixels)
                 indexes (range pixcount)]
             (condp = strat
               :map     (mapv blur indexes)
               :pmap    (into [] (pmap blur indexes))
               :ppmap   (into [] (ppmap 1000 blur indexes))
               :reducer (into [] (r/map blur (vec indexes)))))))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (q/image (q/load-image (str "images/cat-" width ".jpg")) 0 0))

(defn draw [strat]
  (fn []
    (let [pixels  (q/pixels)
          pixelsv (into [] pixels)
          pixelsb (next-image strat pixelsv)]
      (doseq [i (range pixcount)]
        (aset pixels i (get pixelsb i)))
      (q/update-pixels))))

(q/defsketch hello-quil
  :title "It was you, you blur!"
  :size [width height]
                                        ; setup function called only once, during sketch initialization.
  :setup setup
                                        ; update-state is called on each iteration before draw-state.
                                        ; :update update-state
  :draw (draw :map)
  :features [:keep-on-top]
                                        ; This sketch uses functional-mode middleware.
                                        ; Check quil wiki for more info about middlewares and particularly
                                        ; fun-mode.
                                        ;:middleware [m/fun-mode]
)
