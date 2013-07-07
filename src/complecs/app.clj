(ns complecs.app

  "This namespace provides functions which assist in the creation of apps
for LibGDX, and which are integrated with the rest of the complecs/simplecs
API."
  
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.set :as set])
  (:use [complecs.input]
        [simplecs.core]
        [complecs.files]
        [complecs.sprites])
  (:import (com.badlogic.gdx ApplicationAdapter
                             Gdx
                             InputAdapter)
           (com.badlogic.gdx.graphics GL10
                                      Mesh
                                      OrthographicCamera
                                      Texture)
           (com.badlogic.gdx.graphics VertexAttribute
                                      VertexAttributes
                                      VertexAttributes$Usage)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.backends.lwjgl LwjglApplication
                                            LwjglApplicationConfiguration)))

;;;; Some constants
(def LWJGL ::LWJGL)


;;;; Debug options
(def ^:dynamic *app-dbg* #{})

(defmacro dbg-run [opts & r]
  `(binding [*app-dbg* (set ~opts)]
     ~@r))

(defmacro dbg-do [opt & r]
  `(if (*app-dbg* ~opt)
     (do ~@r)))


;;;; Application State
(def ^:dynamic *app-state* nil)

(defmacro app-do
  [atm & r]
  `(binding [*app-state* (deref ~atm)]
     ~@r))

;;;; Utility functions
(defn graphics []
  Gdx/graphics)

(defn delta-time []
  (.getDeltaTime (graphics)))

(defn keys-pressed
  ([] (@ (*app-state* :input-state) :pressed))
  ([k] ((keys-pressed) k)))

(defn keys-released
  ([] (@ (*app-state* :input-state) :released))
  ([k] ((keys-released) k)))

(defn keys-held
  ([] (@ (*app-state* :input-state) :held))
  ([k] ((keys-held) k)))

;;;; Rendering and updating functions
(let [mesh (atom nil)]
  (defsystem test-triangle
    []
    [ces]
    (when-not @mesh
      (reset! mesh
              (Mesh. true (int 3) (int 3)
                            (into-array [(VertexAttribute.
                                          VertexAttributes$Usage/Position 3
                                          "a_position")])))
      
      (.setVertices @mesh (float-array [-0.5 -0.5 0
                                        0.5 -0.5 0
                                        0 0.5 0]))
        
      (.setIndices @mesh (short-array (map short [0 1 2])))
      )

    (.render @mesh GL10/GL_TRIANGLES 0 3) 
    
    ces
    ))

;;;; Rendering functoins take a time difference and return void
(defn blank-screen
  []
  (.glClear Gdx/gl GL10/GL_COLOR_BUFFER_BIT)
  )

;;;; Update functions take 

(defn default-app-state
  "Returns a default application state as a map, which includes the
following fields:
:input-states -> keys pressed, keys held, keys released

The user may provide additional map entries as key-value pairs in the
arguments.  (i.e. (default-app-state :a 1 :b 2 :c 3))
Key-value pairs provided in the arguments will override the defaults."
  [& {:as kvps}]
  (make-ces
   (merge-with
    
    ;; A super-special merge function! 
    (fn [v1 v2]
      (cond
       ;; Two sequences
       (and (seq? v1)
            (seq? v2))
       (concat v1 v2)

       ;; Two vectors
       (and (vector? v1)
            (vector? v2))
       (vec (concat v1 v2))

       ;; Two sets
       (and (set? v1)
            (set? v2))
       (set/union v1 v2)

       ;; Two maps
       (and (map? v1)
            (map? v2))
       (merge v1 v2)

       ;; Other: just select the second argument
       :else
       v2
       )
      )
    
    {:input-state (atom {:held {}
                         :pressed #{}
                         :released #{}})
     :game-state {}
     :entities []
     :systems []
     }
    kvps)))
  
(defn app-set!
  "Sets a value in the app state."
  [app-state keys val]
  (swap! app-state assoc-in keys val))

(defn app-upd!
  "Updates a value in the app state."
  [app-state keys f & args]
  (apply swap! app-state update-in keys f args))


;;;; The engine application adapter

(defn engine-app-adapter
  "Start an application adapter with an app state and function hooks.
Function hooks are sequences of functions of the form:
app-state -> app-state.

They are applied in the following order:
Start of app : startup
Update cycle : pre-update, pre-render, post-update, post-render
Pause : paused
Dispose (end of app) : disposed

Make sure that each function always accepts and returns a valid game state.
If your function is just there for side effects, then return an un-modified
state."
  
  [astate
   &{:keys [startup
            pre-update
            post-update
            pre-render
            post-render
            paused
            disposed]}]

   
   (let [astate-atom (atom astate)
         startup (apply comp (reverse startup))
         pre-update (apply comp (reverse pre-update))
         post-update (apply comp (reverse post-update))
         pre-render (apply comp (reverse pre-render))
         post-render (apply comp (reverse post-render))
         paused (apply comp (reverse paused))
         disposed (apply comp (reverse disposed))
         ]
     
     (proxy [ApplicationAdapter] []
       
      ;;; Creation of the app!
       (create []
         
         ;; It is important to set the input processor to work with our system
         (.setInputProcessor Gdx/input
                             (proxy [InputAdapter] []
                               (keyDown [keycode]
                                 (binding [*app-state* @astate-atom]
                                   (try
                                     (press-key (:input-state *app-state*)
                                                keycode)
                                     (catch Exception e
                                       (println "InputAdapter keydown error: " e)
                                       )))
                                 
                                 true
                                 )
                                                
                               
                               (keyUp [keycode]
                                 (binding [*app-state* @astate-atom]
                                   (try
                                     (release-key (:input-state *app-state*)
                                                  keycode)
                                     (catch Exception e
                                       (println "InputAdapter keyup error: " e)))
                                  
                                   )
                                 true
                                 )
                                       
                               ))
         
         ;; Reduce the application state over the startup functions,
         ;; applying each function
         (swap! astate-atom startup)
         
         )
       
      ;;; This render function is not just a render function,
      ;;; 
       (render []

         
         (swap! astate-atom assoc :delta-time
                (delta-time))
                      
         (app-do astate-atom
                 (swap! astate-atom pre-update))

         (app-do astate-atom
                 (swap! astate-atom pre-render))
         
         (.glClear Gdx/gl GL10/GL_COLOR_BUFFER_BIT)
         
         ;; Rendering takes place as part of the component systems
         (try
           (app-do astate-atom
                   (swap! astate-atom advance-ces))
           (catch Exception e
             (println "ces update exception: " e)))

         (app-do astate-atom
                    (swap! astate-atom post-update))

         (app-do astate-atom
                 (swap! astate-atom post-render))

         (try
           (update-keys (:input-state @astate-atom)
                        (delta-time))
           (catch Exception e
             (println "key update exception: " e)))
         
         )
       
       (pause []
         (app-do astate-atom
                 (swap! astate-atom paused))
         )
       
       (dispose []
         (app-do astate-atom
                 (swap! astate-atom disposed))
         )
       )
     
     ))

;;;; Running the Applications



(defmulti start-app
  ""
  :backend)

(defmethod start-app :default
  [{:keys [backend]}]
  (throw (Exception.
          (str "Error starting app. Backend " backend " not supported."))))
               

(defmethod start-app ::LWJGL
  [{:keys [width
           height
           title
           app-state
           ]
    
    :as m}]

  (let [
        app-state (if (app-state :input-state)
                    app-state
                    (assoc app-state :input-state (atom default-input-state)))
        
        ;; App Configuration
        cfg (LwjglApplicationConfiguration.)
        ;; Variable options
        _ (set! (. cfg title) title)
        _ (set! (. cfg width) width)
        _ (set! (. cfg height) height)
        ;; Constant options
        _ (set! (. cfg useGL20) false)
        _ (set! (. cfg vSyncEnabled) true)
        ;;_ (set! (. cfg forceExit) false)
        ]
    (LwjglApplication. (engine-app-adapter app-state)
                       cfg)))