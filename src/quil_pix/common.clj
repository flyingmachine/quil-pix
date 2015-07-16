(ns quil-pix.common
  (:require [quil.core :as q]
            [taoensso.timbre :as timbre :refer [info]]))

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel
  overhead worthwhile"
  [grain-size f & colls]
  (apply concat
         (apply pmap
                (fn [& pgroups] (doall (apply map f pgroups)))
                (map (partial partition-all grain-size) colls))))

(defn xy
  "Given a linear location and the grid's width, return x and y"
  [i width]
  [(mod i width) (int (/ i width))])

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

(defn draw [next-image]
  (info "draw")
  (fn []
    (let [pixels  (q/pixels)
          pixelsv (into [] pixels)
          pixelsb (next-image pixelsv)]
      (doseq [i (range (count pixels))]
        (aset pixels i (get pixelsb i)))
      (q/update-pixels))))
