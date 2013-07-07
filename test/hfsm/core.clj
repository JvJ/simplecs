(ns hfsm.core
  (:import [com.badlogic.gdx.graphics
            Mesh
            VertexAttribute
            VertexAttributes$Usage
            Color
            GL10]
           [com.badlogic.gdx
            Gdx])
  (:use [simplecs.core]
        [complecs.component-systems]
        [complecs.state-machines]
        [complecs.util]
        [complecs.events]
        [complecs.app]
        [complecs.input]))


(let [r-ary (float-array [(/ (Math/sin (Math/toRadians 240)) 2.0)
                          (/ (Math/cos (Math/toRadians 240)) 2.0)
                          0
                          (Color/toFloatBits 0 0 255 255) 
                          (/ (Math/sin (Math/toRadians 120)) 2.0)
                          (/ (Math/cos (Math/toRadians 120)) 2.0)
                          0
                          (Color/toFloatBits 0 255 0 255)
                          0 0.5 0
                          (Color/toFloatBits 255 0 0 255)])
          
      g-ary (float-array [-0.5 -0.5 0
                          (Color/toFloatBits 0 255 0 255) 
                          0.5 -0.5 0
                          (Color/toFloatBits 255 0 0 255)
                          0 0.5 0
                          (Color/toFloatBits 0 0 255 255)])

      b-ary (float-array [-0.5 -0.5 0
                          (Color/toFloatBits 255 0 0 255) 
                          0.5 -0.5 0
                          (Color/toFloatBits 0 0 255 255)
                          0 0.5 0
                          (Color/toFloatBits 0 255 0 255)])
      ]
  (defn tri-render
    [ecs ent cmp]
    (let [mesh (atom nil)]
      (when-not @mesh
        
        (reset! mesh
                (Mesh. true 3 3
                       (into-array
                        VertexAttribute
                        [(VertexAttribute. VertexAttributes$Usage/Position
                                           3 "a_position")
                         (VertexAttribute. VertexAttributes$Usage/ColorPacked
                                           4 "a_color")])))
        
        
        (.setVertices @mesh r-ary)
        
        (.setIndices @mesh (short-array (map short
                                             [0 1 2]))))
      

      
      (try
        (case (get-in-entity ecs ent [:state-machine :state])
          :R (do
               (.glLoadIdentity Gdx/gl)
               (.glRotatef Gdx/gl 0.0 0.0 0.0 -1.0))
          :G (do
               (.glLoadIdentity Gdx/gl)
               (.glRotatef Gdx/gl 120.0 0.0 0.0 -1.0))
          :B (do
               (.glLoadIdentity Gdx/gl)
               (.glRotatef Gdx/gl 240.0 0.0 0.0 -1.0))
          ;; Default
          nil
          )
        (catch Exception e
          (println "Exception caught switching vertex buffers.")))
        
      ;; Now draw the gash dern thing
      (.render @mesh GL10/GL_TRIANGLES 0 3)
      
      ;; Return the ecs
      ecs
      )))

(defn tri-R
  [ecs ent cmp]
  (cond
   (keys-pressed :Right) (switch-state :G ecs ent cmp)
   (keys-pressed :Left)  (switch-state :B ecs ent cmp)
   :else ecs))

(defn tri-G
  [ecs ent cmp]
  (cond
   (keys-pressed :Right) (switch-state :B ecs ent cmp)
   (keys-pressed :Left)  (switch-state :R ecs ent cmp)
   :else ecs))

(defn tri-B
  [ecs ent cmp]
  (cond
   (keys-pressed :Right) (switch-state :R ecs ent cmp)
   (keys-pressed :Left)  (switch-state :G ecs ent cmp)
   :else ecs))
                   
(def default-ces
  ;; LEFTOFF: The state machine component is causing null pointer exceptions?
  (make-ces {:entities
             [[(renderable tri-render)
               (state-machine :R
                              :R tri-R
                              :G tri-G
                              :B tri-B
                              )
               ]
              ]
             :systems
             [(render)
              (state-machine-update)
              ]}))

(defn main [& args]
  (start-app {:backend LWJGL
              :width 400
              :height 400
              :title "FSM Tests"
              :app-state default-ces}))
