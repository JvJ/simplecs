(defproject complecs "0.1.0-SNAPSHOT"
  :description "Complecs is an entity-component-system game engine based on Simplecs, using LibGDX for graphics and physics."
  :url "https://github.com/JvJ/complecs"
  :licenses [{:name "Apache License 2.0"
              :url "http://www.apache.org/licenses/LICENSE-2.0.html"
              :distribution :repo
              :comments "Dual-licensed. Any of the two licenses may be chosen."}
             {:name "CC0 1.0 Universal"
              :url "http://creativecommons.org/publicdomain/zero/1.0/"
              :distribution :repo
              :comments "Dual-licensed. Any of the two licenses may be chosen."}]
  :repositories [["libgdx" "http://libgdx.badlogicgames.com/nightlies/maven/"]
                 ]

  :plugins [[lein-swank "1.4.5"]]
  
  :dependencies [[org.clojure/algo.generic "0.1.1"]
                 [org.clojure/algo.monads "0.1.4"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/core.incubator "0.1.2"]
                 [com.badlogic.gdx/gdx "0.9.9-SNAPSHOT"]
                 [com.badlogic.gdx/gdx-backend-lwjgl "0.9.9-SNAPSHOT"]
                 ])
