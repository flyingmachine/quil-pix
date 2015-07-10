(ns quil-pix.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 1)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :rgb))

(defn draw []
  ; Clear the sketch by filling it with light-grey color.
  (println "test")
  (let [img (q/create-image 200 200 :argb)]
    (q/set-pixel 100 100 (q/color (q/random 255) (q/random 255) (q/random 255)))))

(q/defsketch hello-quil
  :title "You spin my circle right round"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  ; :update update-state
  :draw draw
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  ;:middleware [m/fun-mode]
  )


(defn close [] (a/applet-close hello-quil))
