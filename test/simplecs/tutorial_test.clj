(ns simplecs.tutorial-test
  (:use [complecs.util])
  (:require [simplecs.core :refer :all]))



(defcomponent ball [] {})

(defcomponent position [pos]
  {:pos pos})

(defcomponent velocity [v]
  {:v v})

(defcomponentsystem mover :velocity
  []
  [ces entity velocity-component]
  (update-entity ces
                 entity
                 [:position :pos]
                 + (:v velocity-component)))

(defcomponentsystem collider :ball
  []
  [ces entity _]
  (letc ces entity
        [pos [:position :pos]
         v [:velocity :v]]
        (if (or (and (< pos 1)
                     (< v 0))
                (and (>= pos (- (:width ces) 1))
                     (> v 0)))
          (update-entity ces entity [:velocity :v] -)
          ces)))

(defcomponentsystem collider-m :ball
  []
  [ces entity component]
  (do-ecs ces entity component
          
          pos (e-get [:position :pos])
          
          v (e-get [:velocity :v])
          
          _ (if (or (and (< pos 1)
                         (< v 0))
                    (and (>= pos (- (:width ces) 1))
                         (> v 0)))
              (e-upd [:velocity :v] - [])
              (m-result nil))))
              

(defcomponentsystem position-validator :ball
  []
  [ces entity _]
  (update-entity ces
                 entity
                 [:position :pos]
                 #(max 0 (min (- (:width ces) 1) %))))

(defsystem output-clearer
  [bg-symbol]
  [ces]
  (assoc ces :output (vec (repeat (:width ces) bg-symbol))))

(defcomponentsystem ball-renderer :ball
  [ball-symbol]
  [ces entity _]
  (assoc-in ces
            [:output (:pos (get-component ces entity :position))]
            ball-symbol))

(defcomponentsystem ping-pong-renderer :ball
  []
  [ces entity _]
  (letc ces entity
        [pos [:position :pos]]
        (cond (= pos 0) (assoc ces :output (vec "PING!"))
              (= pos (- (:width ces) 1)) (assoc ces :output (vec "PONG!"))
              :default ces)))

(defsystem output-converter
  []
  [ces]
  (update-in ces [:output] #(apply str %)))

(def ces
  (atom (make-ces {:entities [[(ball)
                               (position -1)
                               (velocity 1)]]
                   :systems [(mover)
                             ;;(collider)
                             (collider-m)
                             (position-validator)
                             (output-clearer \.)
                             (ball-renderer \o)
                             (ping-pong-renderer)
                             (output-converter)]
                   :width 5})))

(defn update []
  (swap! ces advance-ces)
  (println (:output @ces)))