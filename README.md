# Quil Pix

This tiny library shows performance differences between `map`, `pmap`
a `tiled-pmap` (doing pmap over tiled subcollections) and map with
`reducers/foldcat`.

To run the examples, hop into a repl with `boot repl`, then do something like:

```clojure
(blur :map 300)
```

Where `:map` is the strategy to use, and `300` is the size of the
image to use. Available strategies:

* `:map` serial map
* `:pmap` Clojure's plain ol' `pmap`
* `:tiled-pmap` Group collection into subcollections of 1000, `pmap`
  over those subcollections
* `:reducer` Use `reducers/map` and `reducers/foldcat` for parallel
  mapping

Available sizes:

* 100
* 150
* 300
* 450
* 600

You can run `(close)` at the REPL or just close the window to stop the
animation.

Each image iteration is profiled and logged to "logs.txt". `tail -f
logs.txt` to see how much time the different strategies take.
