# qlearningAgents.py
# ------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from calendar import different_locale
from game import *
from learningAgents import ReinforcementAgent
from featureExtractors import *

import random,util,math

class QLearningAgent(ReinforcementAgent):
    """
      Q-Learning Agent

      Functions you should fill in:
        - computeValueFromQValues
        - computeActionFromQValues
        - getQValue
        - getAction
        - update

      Instance variables you have access to
        - self.epsilon (exploration prob)
        - self.alpha (learning rate)
        - self.discount (discount rate)

      Functions you should use
        - self.getLegalActions(state)
          which returns legal actions for a state
    """
    def __init__(self, **args):
        "You can initialize Q-values here..."
        ReinforcementAgent.__init__(self, **args)

        "*** YOUR CODE HERE ***"

        # pull the q values from the counter
        self.values = util.Counter()

    def getQValue(self, state, action):
      """
      Returns Q(state,action)
      Should return 0.0 if we have never seen a state
      or the Q node value otherwise
      """
      "*** YOUR CODE HERE ***"
      return self.values[(state, action)]



    def computeValueFromQValues(self, state):
        """
          Returns max_action Q(state,action)
          where the max is over legal actions.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return a value of 0.0.
        """
        "*** YOUR CODE HERE ***"

        # get our legal actions that we can do 
        actions = self.getLegalActions(state)

        # if we have no legal actions (in a terminal state) return 0.0
        if len(actions) == 0:
          return 0.0

        # store our q values of the legal actions
        qVals = []

        # find our q values from the legal actions
        for action in actions:
          qVals.append(self.getQValue(state, action))
        
        # return the best q value
        maxQVal = max(qVals)

        return maxQVal







      


    def computeActionFromQValues(self, state):
        """
          Compute the best action to take in a state.  Note that if there
          are no legal actions, which is the case at the terminal state,
          you should return None.
        """
        "*** YOUR CODE HERE ***"

        # get legal actions
        actions = self.getLegalActions(state)

        # if we have no legal actions return None
        if len(actions) == 0:
          return None

        # get our best Q value 
        bestQValue = self.computeValueFromQValues(state)

        # store potentially tied best actions based on Q value
        tiedBestActions = []

        # find the actions that tie the best Q value 
        for action in actions:
          if bestQValue == self.getQValue(state, action):
            tiedBestActions.append(action)

        
        # as instructed, randomly return one of the optimal actions
        return random.choice(tiedBestActions)

        






    def getAction(self, state):
        """
          Compute the action to take in the current state.  With
          probability self.epsilon, we should take a random action and
          take the best policy action otherwise.  Note that if there are
          no legal actions, which is the case at the terminal state, you
          should choose None as the action.

          HINT: You might want to use util.flipCoin(prob)
          HINT: To pick randomly from a list, use random.choice(list)
        """
        # Pick Action
        legalActions = self.getLegalActions(state)
        action = None
        "*** YOUR CODE HERE ***"

        # "an epsilon fraction of the time" simulated by coinflip
        if util.flipCoin(self.epsilon):
          # pick a random action if it hits
          action = random.choice(legalActions)
        
        # if we don't hit the flip, pick the optimal action
        else:
          action = self.computeActionFromQValues(state)        

        # give back the action
        return action

    def update(self, state, action, nextState, reward):
        """
          The parent class calls this to observe a
          state = action => nextState and reward transition.
          You should do your Q-Value update here

          NOTE: You should never call this function,
          it will be called on your behalf
        """
        "*** YOUR CODE HERE ***"

        # our old q value is our current one
        oldQVal = self.values[(state,action)]

        # our new q value is updated with the reward transition to the new state 
        # and the discount applied
        newQVal = reward + (self.computeValueFromQValues(nextState) * self.discount)
        
        # our old and new values from our alpha learning rate variable applied 
        # to the Q value determination
        oldLearn = (1 - self.alpha) * oldQVal
        newLearn = self.alpha * newQVal

        # our updated q value is the sum of the old and new values
        self.values[(state, action)] = oldLearn + newLearn




    def getPolicy(self, state):
        return self.computeActionFromQValues(state)

    def getValue(self, state):
        return self.computeValueFromQValues(state)


class PacmanQAgent(QLearningAgent):
    "Exactly the same as QLearningAgent, but with different default parameters"

    def __init__(self, epsilon=0.05,gamma=0.8,alpha=0.2, numTraining=0, **args):
        """
        These default parameters can be changed from the pacman.py command line.
        For example, to change the exploration rate, try:
            python pacman.py -p PacmanQLearningAgent -a epsilon=0.1

        alpha    - learning rate
        epsilon  - exploration rate
        gamma    - discount factor
        numTraining - number of training episodes, i.e. no learning after these many episodes
        """
        args['epsilon'] = epsilon
        args['gamma'] = gamma
        args['alpha'] = alpha
        args['numTraining'] = numTraining
        self.index = 0  # This is always Pacman
        QLearningAgent.__init__(self, **args)

    def getAction(self, state):
        """
        Simply calls the getAction method of QLearningAgent and then
        informs parent of action for Pacman.  Do not change or remove this
        method.
        """
        action = QLearningAgent.getAction(self,state)
        self.doAction(state,action)
        return action


class ApproximateQAgent(PacmanQAgent):
    """
       ApproximateQLearningAgent

       You should only have to overwrite getQValue
       and update.  All other QLearningAgent functions
       should work as is.
    """
    def __init__(self, extractor='IdentityExtractor', **args):
        self.featExtractor = util.lookup(extractor, globals())()
        PacmanQAgent.__init__(self, **args)
        self.weights = util.Counter()

    def getWeights(self):
        return self.weights

    def getQValue(self, state, action):
        """
          Should return Q(state,action) = w * featureVector
          where * is the dotProduct operator
        """
        "*** YOUR CODE HERE ***"

        # use the feature extractor to get our feature values for state/action pair
        features = self.featExtractor.getFeatures(state, action)

        # track our q value as we iterate through the features
        accumulatedQVal = 0

        # iterate over the features and do the dot product with the weight for each vector
        for feature in features:
          accumulatedQVal += self.weights[feature] * features[feature]

        # return our summed q value
        return accumulatedQVal

        
    def update(self, state, action, nextState, reward):
        """
           Should update your weights based on transition
        """
        "*** YOUR CODE HERE ***"

        # difference equation provided from the instructions
        difference = (reward + (self.discount * self.getValue(nextState)))\
          - self.getQValue(state, action)
        
        # get our feature values for the state/action pair
        features = self.featExtractor.getFeatures(state, action)
        
        # update our weight values for each feature using our difference calculation
        for feature in features:
          self.weights[feature] = self.weights[feature] + self.alpha * difference * features[feature]
          



    def final(self, state):
        "Called at the end of each game."
        # call the super-class final method
        PacmanQAgent.final(self, state)

        # did we finish training?
        if self.episodesSoFar == self.numTraining:
            # you might want to print your weights here for debugging
            "*** YOUR CODE HERE ***"
            pass
