(ns quil-pix.reducers
  "This is a scratch pad that's really just the code from
  http://adambard.com/blog/clojure-reducers-for-mortals/"
  (:require [clojure.core.reducers :as r]
            [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)]))

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
(println "Eager test:    " (benchmark eager-test 100000 10) "ms")
(println "Lazy test:     " (benchmark lazy-test 100000 10) "ms")
(println "Reducers test: " (benchmark reducer-test 100000 10) "ms")


(dorun (profile :info :map
                (dotimes [n 10] (doall (map inc (vec (range 100000)))))))
(dorun (profile :info :rmap
                (dotimes [n 10] (into [] (r/map inc (vec (range 100000)))))))
