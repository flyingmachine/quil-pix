(set-env!
 :source-paths   #{"src"}
 :dependencies '[[adzerk/boot-reload    "0.2.6"      :scope "test"]
                 [quil "2.2.6"]
                 [com.taoensso/timbre "4.0.2"]])

(require '[quil.applet :as a]
         '[quil-pix.blur :as b])

(defn close [x] (a/applet-close x))
