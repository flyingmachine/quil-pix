(ns quil-pix.scan
  (:require [quil.core :as q]           
            [clojure.core.reducers :as r]
            [taoensso.timbre :as timbre :refer [info]]
            [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)]
            [taoensso.timbre.appenders.core :as appenders]
            [clojure.zip :as zip]
            [quil-pix.common :refer [ppmap xy xy->i random-color rgb draw setup avg-colors]]))

(defn zip-row
  [cur-row-loc prev-row]
  (loop [i 1
         left-loc (zip/down cur-row-loc)
         cur-loc  (zip/right left-loc)]
    (if cur-loc
      (let [updated (zip/edit cur-loc (constantly (avg-colors [(zip/node left-loc)
                                                               (zip/node cur-loc)
                                                               (get prev-row i)])))]
        (recur (inc i)
               updated
               (zip/right updated)))
      (-> left-loc zip/up zip/node))))

;; TODO factor out repetition between the two hierarchy levels
(defn zip-pixels [width pixels]
  (let [pixel-grid (zip/vector-zip (vec (map vec (partition-all width pixels))))]
    (loop [prev-row-loc (zip/down pixel-grid)
           prev-row     (zip/node prev-row-loc)
           cur-row-loc  (zip/right prev-row-loc)]
      (if cur-row-loc
        (let [updated-row (zip/edit cur-row-loc
                                    (constantly (zip-row cur-row-loc prev-row)))]
          (recur updated-row
                 (zip/node updated-row)
                 (zip/right updated-row)))
        (-> prev-row-loc zip/root)))))

(defn reduce-pixels [width pixels]
  (reduce (fn [x y]
            (if (= 1 (mod (count x) width))
              (conj x y)
              (conj x (avg-colors [y (get x (- (count x) width)) (last x)]))))
          (subvec pixels 0 (inc width))
          (subvec pixels (inc width))))

(defn next-image
  [strat width pixels]
  (try
    (profile :info strat
             (condp = strat
               :zipper  (vec (apply concat (zip-pixels width pixels)))
               :reduce  (reduce-pixels width pixels)))
    (catch Exception e (info "exception: " (.getMessage e)))))

(defonce pixels (atom nil))
(defonce graphics (atom nil))

(defn sketch
  [strat width]
  (q/sketch
   :title "Scan"
   :size [width width]
                                        ; setup function called only once, during sketch initialization.
   :setup (setup width)
                                        ; update-state is called on each iteration before draw-state.
                                        ; :update update-state
   :draw (draw #(next-image strat width %))
   :features [:keep-on-top]))
