A little playground for exploring Clojure performance. After starting
the REPL, you can run `(run-blur {strat})` where `strat` is one of
`:map`, `:pmap`, `:ppmap`, or `:reducer`. All the real code is in
`blur.clj`.

You can run `(close)` at the REPL or just close the window to stop the
animation.

Each image iteration is profiled and logged to "logs.txt".
