(ns complecs.input
  (:use [clojure.algo.generic.functor])
  (:import (com.badlogic.gdx Input$Keys)))

(def keys-map
  {Input$Keys/A \A
   Input$Keys/B \B
   Input$Keys/C \C
   Input$Keys/D \D
   Input$Keys/E \E
   Input$Keys/F \F
   Input$Keys/G \G
   Input$Keys/H \H
   Input$Keys/I \I
   Input$Keys/J \J
   Input$Keys/K \K
   Input$Keys/L \L
   Input$Keys/M \M
   Input$Keys/N \N
   Input$Keys/O \O
   Input$Keys/P \P
   Input$Keys/Q \Q
   Input$Keys/R \R
   Input$Keys/S \S
   Input$Keys/T \T
   Input$Keys/U \U
   Input$Keys/V \V
   Input$Keys/W \W
   Input$Keys/X \X
   Input$Keys/Y \Y
   Input$Keys/Z \Z
   Input$Keys/LEFT :Left
   Input$Keys/RIGHT :Right
   Input$Keys/UP :Up
   Input$Keys/DOWN :Down
   Input$Keys/SPACE :Space
   Input$Keys/ESCAPE :Esc
   })

(def default-input-state
  {:pressed #{}
   :released #{}
   :held {}})

(defn press-key
  "Takes a map of objects to update."
  [input-state
   k-code]
  (let [k (keys-map k-code)]
    (swap! input-state update-in [:pressed] conj k)
    (swap! input-state update-in [:held] assoc k 0)))
          
(defn release-key
  "Takes a map of atoms to update and a key code."
  [input-state
   k-code]
  (let [k (keys-map k-code)]
    (swap! input-state update-in [:pressed] disj k)
    (swap! input-state update-in [:held] dissoc k)
    (swap! input-state update-in [:released] conj k)))

(defn update-keys
  "Takes an atom to update and a time difference."
  [input-state
   t-diff]
  (swap! input-state update-in [:pressed] empty)
  (swap! input-state update-in [:released] empty)
  (swap! input-state update-in [:held]
         (fn [m] (fmap #(+ % t-diff)
                       m))))