(ns quil-pix.reducers
  (:require [clojure.core.reducers :as r]))

(defn benchmark [f N times]
  (let [nums (vec (range N))
        start (java.lang.System/currentTimeMillis)]
    (dotimes [n times]
      (f nums))
    (- (java.lang.System/currentTimeMillis) start)))
  
(defn eager-map
  "A dumb map"
  [& args]
  (doall (apply map args)))

(defn eager-filter
  "An eager filter"
  [& args]
  (doall (apply filter args)))

(defn eager-test [nums]
  (eager-map inc nums))

(defn lazy-test [nums]
  (doall (map inc nums)))


(defn reducer-test [nums]
  (into [] (r/map inc nums)))

(println "Eager v. Lazy v. Reducer filter+map, N=1000000, 10 repetitions")
(println "Eager test:    " (benchmark eager-test 1000000 10) "ms")
(println "Lazy test:     " (benchmark lazy-test 1000000 10) "ms")
(println "Reducers test: " (benchmark reducer-test 1000000 10) "ms")
