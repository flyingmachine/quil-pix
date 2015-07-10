(ns quil-pix.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]))

(def width 10)
(def height 10)
(def pixcount (* width height))

(defn xy [i row-len]
  [(mod i row-len) (int (/ i row-len))])

(defn neighbors [i count rad row-len]
  (let [[x1 y1] (xy i row-len)
        m  (* -1 rad)
        n  (inc rad)]
    (for [x2 (range m n)
          :let [x (+ x1 x2)]
          :when (> x -1)
          
          y2 (range m n)
          :let [y (+ y1 y2)]
          :when (> y -1)]
      [x y])))

(defn random-color []
  (q/color (q/random 255) (q/random 255) (q/random 255)))

(defn cell-i [[x y] row-len]
  (+ x (* y row-len)))

(defn blur [i rad row-len pixels]
  (let [cells (neighbors i pixcount rad width)]

    
    (random-color)))

(defn draw []
  (let [pixels  (q/pixels)
        pixelsb (mapv #(blur % 3 width pixels) (range pixcount))
        img (q/create-image width height :argb)]
    (doseq [i (range pixcount)]
      (let [[x y] (xy i width)]
        (q/set-pixel x y (get pixelsb i))))))

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
