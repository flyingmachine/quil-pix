(ns quil-pix.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]))

(def width 500)
(def height 500)
(def pixcount (* width height))

(defn neighbors [i count rad row-len]
  (let [x1 (mod i row-len)
        y1 (int (/ i row-len))
        m  (* -1 rad)
        n  (inc rad)]
    (for [x2 (range m n)
          :let [x (+ x1 x2)]
          :when (> x -1)
          
          y2 (range m n)
          :let [y (+ y1 y2)]
          :when (> y -1)]
      [x y])))

(defn cell-i [[x y] row-len]
  (+ x (* y row-len)))

(defn blur [i rad row-len pixels]
  (let [cells (neighbors i pixcount rad width)
        weight (/ 1 (count cells))]
    (apply + (map #(* weight %)
                  (map #(aget pixels (cell-i % row-len)) cells)))))

(defn draw []
  (let [indexes (range pixcount)
        pixels  (q/pixels)
        pixelsb (mapv #(blur % 3 width pixels) indexes)]
    (doseq [i indexes]
      (aset pixels i (q/color (q/random 255) (q/random 255) (q/random 255))))
    (q/update-pixels)))

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
