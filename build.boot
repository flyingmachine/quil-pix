(set-env!
 :source-paths   #{"src"}
 :dependencies '[[adzerk/boot-reload    "0.2.6"      :scope "test"]
                 [quil "2.2.6"]
                 [com.taoensso/timbre "4.0.2"]
                 [org.clojure/clojure "1.7.0"]])

(require '[quil.applet :as a]
         '[quil-pix.blur :as b])

(def sketch (atom nil))
(defn run-sketch [sketch-fn & args]
  (reset! sketch (apply sketch-fn args)))

(defn run-blur [& args]
  (apply run-sketch b/blur-sketch args))

(defn close [] (a/applet-close @sketch))
