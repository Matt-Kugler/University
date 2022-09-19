Here is my implementation of the [third berkely pacman project](https://inst.eecs.berkeley.edu/~cs188/fa19/project3/). This project required us to implement value iteration and Q learning. A simulated robot controller and Pacman were used and the agents were tested on the common Gridworld reinforcmeent learning model. The value iteration state update equation was utilized to complete this assignment, and was implmeented appropriately with Gridworld examples. Policies were also implemented in this project, with terminal states corresponding to positive payoffs and negative payoffs. In addition, an asynchronous value iteration agent was implemented which only updates one state in each iteration instead of a batch-style update. This project gave a good example of implementing Q learning in an artificial intelligence context and learned through trial and error the best way for pacman to navigate through the maze. I received full marks for this project after running the submission_autograder.py.