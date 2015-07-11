(ns quil-pix.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [quil.applet :as a]
            [taoensso.timbre :as timbre
             :refer (log  trace  debug  info  warn  error  fatal  report
                          logf tracef debugf infof warnf errorf fatalf reportf
                          spy get-env log-env)]
            [taoensso.timbre.profiling :as profiling
             :refer (pspy pspy* profile defnp p p*)]
            [taoensso.timbre.appenders.core :as appenders]))


(timbre/merge-config!
 {:appenders {:spit (appenders/spit-appender {:fname "logs.txt"})}})


(def width 150)
(def height 150)
(def pixcount (* width height))

(defn xy [i row-len]
  [(mod i row-len) (int (/ i row-len))])

(defn neighbors [i count rad row-len rows]
  (let [[x1 y1] (xy i row-len)
        m  (* -1 rad)
        n  (inc rad)]
    (for [x2 (range m n)
          :let [x (+ x1 x2)]
          :when (and (> x -1) (< x row-len))
          
          y2 (range m n)
          :let [y (+ y1 y2)]
          :when (and (> y -1) (< y rows))]
      [x y])))

(defn random-color []
  (q/color (q/random 255) (q/random 255) (q/random 255)))

(defn cell-i [[x y] row-len]
  (+ x (* y row-len)))

(defn blur [i rad row-len pixels]
  (let [cells (neighbors i pixcount rad width height)
        cc    (count cells)]
    (/ (apply + (map #(get pixels (cell-i % row-len))
                     cells))
       cc)))

(defn ppmap
  "Partitioned pmap, for grouping map ops together to make parallel
  overhead worthwhile"
  [grain-size f & colls]
  (apply concat
         (apply pmap
                (fn [& pgroups] (doall (apply map f pgroups)))
                (map (partial partition-all grain-size) colls))))



(defn next-image
  [pixels]
  (into [] (pmap #(blur % 3 width pixels) (range pixcount))))

(defn next-image
  [pixels]
  (mapv #(blur % 3 width pixels) (range pixcount)))

(defn next-image
  [pixels]
  (into [] (ppmap 100 #(blur % 3 width pixels) (range pixcount))))

(defn draw1 []
  (info "drawing")
  (let [pixels  (q/pixels)
        pixelsb (profile :info :next-image (next-image pixels))
        img (q/create-image width height :argb)
        ips (partition-all 100 (range pixcount))]
    (doseq [f (doall (map (fn [ip]
                            (future (doseq [i ip]
                                      (let [[x y] (xy i width)]
                                        (q/set-pixel x y (get pixelsb i))))))
                          ips))]
      @f)))

(defn draw2 []
  (let [pixels  (q/pixels)
        pixelsb (next-image pixels)
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
  :draw draw1
  :features [:keep-on-top]
                                        ; This sketch uses functional-mode middleware.
                                        ; Check quil wiki for more info about middlewares and particularly
                                        ; fun-mode.
                                        ;:middleware [m/fun-mode]
  )


(defn close [] (a/applet-close hello-quil))
