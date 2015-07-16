(set-env!
 :source-paths   #{"src"}
 :dependencies '[[adzerk/boot-reload    "0.2.6"      :scope "test"]
                 [quil "2.2.6"]
                 [com.taoensso/timbre "4.0.2"]
                 [org.clojure/clojure "1.7.0"]])

(require '[quil.applet :as a]
         '[quil-pix.blur :as b]
         '[quil-pix.dim  :as d]
         '[taoensso.timbre :as timbre]
         '[taoensso.timbre.appenders.core :as appenders])

(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "logs.txt"})}})

(def sketch (atom nil))
(defn run-sketch [sketch-fn & args]
  (reset! sketch (apply sketch-fn args)))

(defn run-blur [strat & [width]]
  (run-sketch b/sketch strat (or width 150)))

(defn run-dim [strat & [width]]
  (run-sketch d/sketch strat (or width 150)))

(defn close [] (a/applet-close @sketch))
