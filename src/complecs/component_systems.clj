(ns complecs.component-systems

  "Components and systems that are useful for game creation."
  
  (:use [simplecs.core]
        [complecs.util]
        [complecs.input]
        [complecs.app]
        [complecs.events]))


;;;; Renderable

(defcomponent renderable
  "Components which are renderable.  Func should take 3 arguments:
  [ces entity component], and should return an updated ces."
  [func]
  {:func func})

(defcomponentsystem
  render :renderable
  "Rendering function for renderable components."
  [] ; Parameters?  Don't think so.
  [ces entity component]

  (let [ret ( (component :func)
              ces entity component )]
    ;; Basically, just execute the function
    
    ret))

  


;;;; Timer

(defcomponent timer
  "Countdown timer.  Create a timer with a key path and an event to
trigger on that key path.  It will fire after the count-down.
The trigger function should accept an ecs and event descriptor.
The default trigger just triggers the event.

A timer should be spawned in its own entity!!
"
  ([ecs ks evt count-down]
     (timer ecs count-down
            (fn [ecs evt]
              (evt-trigger ecs ks evt))))
  ([ecs ks evt count-down trigger]
     {:count-down count-down
      :time count-down
      :trigger trigger
      :event evt}
     ))

(defcomponentsystem timer-update :timer
  []
  [ecs ent cmp]
  (do-ecs ecs ent cmp
          
          _ (e-upd [:timer :time] - (ecs :delta-time))

          t (e-get [:timer :time])
          
          _ (if (<= t 0)
              (ecs-upd [] (:trigger cmp) (:event cmp))
              (m-result nil))

          _ (if (<= t 0)
              (e-remove ent)
              (m-result nil))))
          
        
   
   
  
  