(ns quil-pix.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]))

(def dimensions [500 500])
(def width 500)
(def height 500)

(defn draw []
  (let [pixels (q/pixels)]
    ()))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (let [img (q/create-image width height :argb)]
    (doseq [[x y] (for [x (range width) y (range height)] [x y])]
      (q/set-pixel x y (q/color (q/random 255) (q/random 255) (q/random 255))))))

(q/defsketch hello-quil
  :title "You spin my circle right round"
  :size [width height]
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
