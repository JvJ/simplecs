(ns complecs.util

  "Utility functions, including state monad functions for
dealing with the ECS.

State monad functions expect the following state format:
[ces entity component], as a single parameter.
"


  (:use [simplecs.core]
        [clojure.algo.monads])
  )


;;;; State Monad Functions

(defn nop
  "Take no action."
  []
  (fn [ [ces entity component] ]
    [nil
     [ces entity component]]))

(defn e-this
  "Sate monad function.  Returns this entity."
  []
  (fn [ [ces entity component] ]
    [ entity
     [ces entity component]]))

(defn e-get
  "State-monad function.  Wrapper for get-in-entity.
When given a path, it looks up a field in 'this' entity.
Otherwise, you provide an entity and a path."
  ([path] (fn [ [ces entity component :as s] ]
            [ (get-in-entity ces entity path)
              s ]))
  ([ent path]
     (fn [ [ces entity component :as s] ]
       [ (get-in-entity ces ent path)
         s ]
       )))

(defn e-upd
  "State monad function.  Update a field in an entity.  Semantics are
similar to update-entity, but args are provided as an explicit seq rather
than a rest argument."
  ([keyword-or-list f args]
     (fn [[ces entity component :as s]]
       [ nil
        [(apply update-entity ces entity keyword-or-list f args) entity component] ]
       )
     )
  ([ent keyword-or-list f args]
     (fn [[ces entity component :as s]]
       [ nil
        [(apply update-entity ces ent keyword-or-list f args) entity component] ]
       )
     ))

(defn e-set
  "State monad function.  Set a field in an entity to a value."
  ([keyword-or-list val]
     (e-upd keyword-or-list (constantly val) [])
     )
  ([ent keyword-or-list val]
     (e-upd ent keyword-or-list (constantly val) []))
  )

(defn e-remove
  "State monad function.  Remove an entity."
  [ent]
  (fn [[ecs ent cmp]]
    [nil
     [(remove-entity ecs ent) ent cmp]]))

(defn ecs-upd
  "State monad function.  Update a field in the ces."
  ([path f & args]
     (fn [[ces e c :as s]]
       [ nil
        [(apply update-in ces path f args) e c] ])))

(defn ecs-set
  "State monad function.  Set a field in the ces."
  ([path val]
     (fn [[ces e c :as s]]
       [ nil
        [(assoc-in ces path val) e c] ])
     ))

(defn st-cmp
  "Sync's the state's current component and returns it."
  []
  (fn [[ecs ent cmp]]
    (let [cmp (get-component ent (:name cmp))]
      [cmp
       [ecs ent cmp]])))

(defn st-ecs
  "Return the state's ecs."
  []
  (fn [[ecs ent cmp]]
    [ecs
     [ecs ent cmp]]))

(defn st-ent
  "Returns the state's entity."
  []
  (fn [[ecs ent cmp]]
    [ent
     [ecs ent cmp]]))
        

(defmacro do-ecs
  "Use the state monad to modify the ecs."
  [ecs entity component & r]
  `(first
    (second
     ((domonad state-m
               [~@r]
               nil)
      ~[ecs entity component]))))


(defn ancestor-trace
  "Recursively gets the ancestors, in bottom-up order, of k"
  [k]
  (let [a (parents k)]
    (if (empty? a)
      (list k)
      (lazy-seq
       (cons k (ancestor-trace (first a)))))))

;;;; Other functions
(defn q
  "Put a vector in this."
  ([sq]
     `(reduce conj
              clojure.lang.PersistentQueue/EMPTY
              ~sq)))

(defn queue
  "Construct a queue."
  [& r]
  (reduce conj
          clojure.lang.PersistentQueue/EMPTY
          r))