# multiAgents.py
# --------------
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


from argparse import Action
from lib2to3.pgen2.token import EQUAL
from ssl import ALERT_DESCRIPTION_HANDSHAKE_FAILURE
from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        ghostPositions = successorGameState.getGhostPositions()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        # put all our food in a list
        newFoodList = newFood.asList()

        # variable to track the currently closest food
        minFood = 100000000000000

        # iterate through all our food, find the min distance for our next move. 
        for food in newFoodList:
            minFood = min(minFood, manhattanDistance(newPos, food))
        
        # if the ghost gets too close don't go that way
        for ghost in ghostPositions:
            if (manhattanDistance(newPos, ghost) < 4):
                return -100000000000000
        
        # return the reciprocal value of our current score + the potential 
        return successorGameState.getScore() + (1.0 / float(minFood))



def scoreEvaluationFunction(currentGameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"

        # minimax uses a minvalue and maxvalue function
        # maxvalue function for pacman
        def maxValue(state, agentIndex, depth):

            # pacman is always agent 0
            agentIndex = 0

            # get our legal actions
            legalMoves = state.getLegalActions(agentIndex)

            # if there's no legal moves, or we've reached our max depth, or game is over, return utility
            if not legalMoves or depth == self.depth or gameState.isLose() or gameState.isWin():
                return self.evaluationFunction(state)

            # initialize our max value to neg infinity (really small number)
            maxVal = -10000000000

            # if we're not at the end, iterate through the moves and apply minimax
            maxvals = []
            maxvals.append(maxVal)

            # check the paths for all our legal moves from this point
            for move in legalMoves:
                maxvals.append(minValue(state.generateSuccessor(agentIndex, move), agentIndex + 1, depth + 1))

            # get the max path for pacman to choose
            return max(maxvals)

        # minvalue function for the ghosts
        def minValue(state, agentIndex, depth):

            # get our number of agents (ghosts + pacman)
            agentCount = gameState.getNumAgents()
            legalMoves = state.getLegalActions(agentIndex)

            # if we have no legal moves or game is over return our evaluation function
            if not legalMoves or gameState.isLose() or gameState.isWin():
                return self.evaluationFunction(state)

            # initialize min value to neg inifinty (really big num)
            minVal = 10000000000
            

            # if we're handling ghosts  
            if agentIndex != agentCount - 1:

                # track our minvalues from this point and go with the best (minimized) one
                minVals = []
                minVals.append(minVal)
                for move in legalMoves:
                    minVals.append(minValue(state.generateSuccessor(agentIndex, move), agentIndex + 1, depth))
                
                minimumVal = min(minVals)
            
            # otherwise we're dealing with pacman
            else:

                # want to check pacmans min values for the current state and go with that
                minVals = []
                minVals.append(minVal)
                for move in legalMoves:
                    minVals.append(maxValue(state.generateSuccessor(agentIndex, move), agentIndex, depth))

                minimumVal = min(minVals)

            return minimumVal

        
        # Goal: maximize our root node (pacman)
        
        # initialize our moves set
        allMoves = {}

        # get all our moves from index 0 bc it's pacman
        moves = gameState.getLegalActions(0)

        # iterate through our next moves with the appropriate index and depth
        for move in moves:
            allMoves[move] = minValue(gameState.generateSuccessor(0, move), 1, 1) 

        # get the highest scoring action and execute it
        return max(allMoves, key=allMoves.get)
        





    
class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"

        # alpha beta pruning is very similar to expectimax but with alpha 
        # and beta values to prune search space. 
        # maxvalue function for pacman
        def maxValue(state, agentIndex, depth, alpha, beta):

            # pacman is always agent 0
            agentIndex = 0

            # get our legal actions
            legalMoves = state.getLegalActions(agentIndex)

            # if there's no legal moves, or we've reached our max depth, or game is over, return utility
            if not legalMoves or depth == self.depth or gameState.isLose() or gameState.isWin():
                return self.evaluationFunction(state)

            # initialize our max value to neg infinity (really small number)
            maxVal = -10000000000
            

            # initialize our alpha for pacman
            currAlpha = alpha

            # if we're not at the end, iterate through the moves and apply minimax
            # check the paths for all our legal moves from this point
            for move in legalMoves:

                maxvals = []
                maxvals.append(maxVal)
                
                maxvals.append(minValue(state.generateSuccessor(agentIndex, move), agentIndex + 1, depth + 1, \
                    currAlpha, beta))
                
                maxVal = max(maxvals)
                # if our maxvalue is higher than the beta, return that now
                # (pruning)
                if maxVal > beta:
                    return maxVal
                
                # update our current alpha if it needs it
                currAlpha = max(maxVal, currAlpha)
            
            # return maxval (optimal move for pacman)
            return maxVal

        # minvalue function for the ghosts
        def minValue(state, agentIndex, depth, alpha, beta):

            # get our number of agents (ghosts + pacman)
            agentCount = gameState.getNumAgents()
            legalMoves = state.getLegalActions(agentIndex)

            # if we have no legal moves or game is over return our evaluation function
            if not legalMoves or gameState.isLose() or gameState.isWin():
                return self.evaluationFunction(state)

            # initialize min value to neg inifinty (really big num)
            minVal = 10000000000

            # initialize our current beta value for the ghosts
            currBeta = beta

            # if we're handling ghosts  
            if agentIndex != agentCount - 1:

                # track our minvalues from this point and go with the best (minimized) one
                for move in legalMoves:

                    minVals = []
                    minVals.append(minVal)
                    
                    minVals.append(minValue(state.generateSuccessor(agentIndex, move), agentIndex + 1, depth, \
                        alpha, currBeta))

                    minVal = min(minVals)

                    # if our minval is lower than our alpha, prune the tree by returning the min val now
                    if minVal < alpha:
                        return minVal
                    
                    # update our current beta
                    currBeta = min(minVal, currBeta)
            
            # otherwise we're dealing with pacman
            else:

                # want to check pacmans min values for the current state and go with that
                for move in legalMoves:

                    minVals = []
                    minVals.append(minVal)
                    
                    minVals.append(maxValue(state.generateSuccessor(agentIndex, move), agentIndex, depth, \
                        alpha, currBeta))
                    
                    minVal = min(minVals)

                    # if our minval is lower than our alpha, prune the tree by returning the min val now
                    if minVal < alpha:
                        return minVal
                    
                    # update our current beta
                    currBeta = min(minVal, currBeta)

            # return the minval (optimal move for ghosts)
            return minVal

        
        # Goal: maximize our root node (pacman)

        # initialize our alpha and beta values as big and small numbers
        initialAlpha = -10000000000
        initialBeta = 10000000000

        # initialize our moves set
        allMoves = {}

        # get all our moves from index 0 bc it's pacman
        moves = gameState.getLegalActions(0)

        # iterate through our next moves with the appropriate index and depth
        for move in moves:
            
            val = minValue(gameState.generateSuccessor(0, move), 1, 1, initialAlpha, initialBeta) 
            allMoves[move] = val
            # prune our tree if we can do it right away
            if val > initialBeta:
                return move
            
            # update our alpha with the new max
            initialAlpha = max(val, initialAlpha)

            
        # get the highest scoring action and execute it
        return max(allMoves, key=allMoves.get)
        

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"

        # maxValue is similar to minimax and pruning questions
        # trying to maximize pacman's expected value here
        def maxValue(state, agentIndex, depth):

            # pacman is always agent 0
            agentIndex = 0

            # get our legal actions
            legalMoves = state.getLegalActions(agentIndex)

            # if there's no legal moves, or we've reached our max depth, or game is over return utility
            if not legalMoves or depth == self.depth or gameState.isLose() or gameState.isWin():
                return self.evaluationFunction(state)

            # initialize our max value to neg infinity (really small number)
            maxVal = -10000000000

            # if we're not at the end, iterate through the moves and apply minimax
            maxvals = []
            maxvals.append(maxVal)

            # check the paths for all our legal moves from this point
            for move in legalMoves:
                maxvals.append(expectedValue(state.generateSuccessor(agentIndex, move), agentIndex + 1, depth + 1))

            # get the max path for pacman to choose
            return max(maxvals)
        
        # our expected value function for pacman and the ghosts, pretty similar idea to the 
        # previously used minValue functions but now ghosts choose actions uniformly randomly 
        def expectedValue(state, agentIndex, depth):

            # get our number of agents (ghosts + pacman)
            agentCount = gameState.getNumAgents()
            legalMoves = state.getLegalActions(agentIndex)

            # if we have no legal moves or game is over return our evaluation function
            if not legalMoves or gameState.isLose() or gameState.isWin():
                return self.evaluationFunction(state)

            # initialize expected chance of move to 0 
            expectVal = 0

            # adversary chooses moves uniformly at random
            moveChance = 1.0 / len(legalMoves)
            
            # iterate over all our moves and check if we're dealing with pacman or ghosts
            for move in legalMoves:
                # if we're handling ghosts, use the expected value algorithm for their next moves
                if agentIndex != agentCount - 1:
                    currExpectedVal = expectedValue(state.generateSuccessor(agentIndex, move), agentIndex + 1, depth)
            
                # otherwise we're dealing with pacman, want his max value
                else:
                    currExpectedVal = maxValue(state.generateSuccessor(agentIndex, move), agentIndex, depth)

                # expected values for the moves including the probability of a move chance. 
                expectVal += currExpectedVal * moveChance
            
            # return our expected value for the move
            return expectVal


        
        # Goal: maximize our root node (pacman)
        
        # initialize our moves set
        allMoves = {}

        # get all our moves from index 0 bc it's pacman
        moves = gameState.getLegalActions(0)

        # iterate through our next moves with the appropriate index and depth to get their expected values
        for move in moves:
            allMoves[move] = expectedValue(gameState.generateSuccessor(0, move), 1, 1) 

        # get the highest scoring action and execute it
        return max(allMoves, key=allMoves.get)
        



        






def betterEvaluationFunction(currentGameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"

    # some helpful gamestate stuff
    currPos = currentGameState.getPacmanPosition()
    currFoodList = currentGameState.getFood().asList()

    # track our current closest food (-1 at first because we don't know what it will be)
    closestFood = -1

    # calculate the distance to foods, update closest one. 
    for food in currFoodList:
        if closestFood >= util.manhattanDistance(currPos, food) or closestFood == -1:
            closestFood = util.manhattanDistance(currPos, food)
    
    # calculate distance from pacman to ghosts and check number of ghosts in his proximity
    ghostDistance = 1
    ghostProximity = 0

    # update ghost distance from pacman and the number of them in his proximity
    for ghost in currentGameState.getGhostPositions():
        ghostDistance += util.manhattanDistance(currPos, ghost)
        
        # if the ghost is within pacman's proximity, update the number in his proximity
        if util.manhattanDistance(currPos, ghost) <= 1:
            ghostProximity += 1
    
    # remaining capsules subtract from the score because we need all of them
    numCapsules = len(currentGameState.getCapsules())

    # evaluation function with all the above calculations, use the reciprocal as with the first 
    # evalutation function 
    currScore = currentGameState.getScore() + (1.0 / closestFood) - (1.0 / ghostDistance) \
        - ghostProximity - numCapsules
    
    return currScore




# Abbreviation
better = betterEvaluationFunction
