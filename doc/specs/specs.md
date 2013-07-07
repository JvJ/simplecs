# Complecs

## Motivation

Before I continue, I'd like to talk a little bit about my motivation.
Clearly, there are a number of excellent and widely available game
engines on the market today... I'm mostly just doing it as a hobby.
Because I feel like it.

Also for the Lisp In Summer Projects (LISP) competition (http://http://lispinsummerprojects.org/).

## Goals

Our goal with complecs is to create a simple, command-driven,
interactive game engine that can be used with a REPL.  Users will be
able to use the game environment directly to edit their games, and
will be able to switch between running and editing a game.

Here are some of the major features:

### Entity-Component-System Architecture

A nifty way to model entities in the game engine that differs from
object-orientation.

### Command-Driven

Rather than provide an extensive GUI, editing will be accomplished
primarily through functions in the REPL, as well as key bindings in
the engine's editor mode that 

### Interactive

Developers will be able to make changes to the game while it is
running.  The engine will have two modes: editor mode and game mode.
Editor mode is, in fact, just a special condition of game mode where
certain update cycles are not executing.

### Customizable

Although a rich set of default functions and key-bindings to those
functions will be provided, the user is completely free to write thier
own functions and provide their own key bindings, making the engine
extremely extensible.

### Cross-Platform

Thanks to LibGDX, the games can be developed in a desktop environment
and subsequently ported to other platforms, such as HTML5 and Android.

### Other Features
* Clojure!
* Functional!  Game states are modified in a side-effect-free
functional way.  Side effects only occur once every update cycle.
This means that the game state is one big data structure.  It's easy
to keep track of different game states, and easy to
serialize/de-serialize.