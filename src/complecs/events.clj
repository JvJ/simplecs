(ns complecs.events

  "This namespace provides components and systems for handling events."

  (:use [simplecs.core]
        [clojure.algo.monads])
  
  )

(defsystem event-system
  "This system manages the event updates."
  []
  [ces]
  (->
   ces
   (assoc-in [:events :current]
             (get-in ces [:events :next]))
   (assoc-in [:events :next nil])))

(defn- v-conj
  [coll x & xs]
  (if coll
    (apply conj coll x xs)
    (apply vector x xs)))

(defn evt-trigger
  "Trigger an event!"
  [ces [k & _ :as ks] obj]
  (assoc-in ces (cons :events ks) obj))

(defn evt-conj
  [ces [k & _ :as ks] obj]
  (update-in ces (cons :events ks) v-conj obj))

(defn evt-get
  [ces [k & _ :as ks]]
  (get-in ces ks))

(defn evt-trigger-m
  "State monad function for evt-trigger."
  [[k & _ :as ks] obj]
  (fn [[ecs ent cmp]]
    [nil
     [(evt-trigger ecs ks obj) ent cmp]]))

(defn evt-conj-m
  "State monad function for evt-trigger."
  [[k & _ :as ks] obj]
  (fn [[ecs ent cmp]]
    [nil
     [(evt-conj ecs ks obj) ent cmp]]))

(defn evt-get-m
  ""
  [[k & _ :as ks]]
  (fn [[ecs ent cmp]]
    [(evt-get ecs ks)
     [ecs ent cmp]]))