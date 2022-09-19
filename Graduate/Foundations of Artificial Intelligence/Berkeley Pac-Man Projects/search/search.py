# search.py
# ---------
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


"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

from platform import node
from queue import Empty
from tracemalloc import start
import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]

def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:
    """

    # DFS is a last in first out queue, or a stack
    # going to track the node and its list of actions 
    nodeStack = util.Stack()

    # need to maintain a list of explored nodes
    exploredSet = [] 

    # need our start state, and to check if it's already at the end, returning no actions
    startState = problem.getStartState()
    if problem.isGoalState(startState):
        return []

    # since our start state isn't a goal, push it to the stack with an empty list of actions
    nodeStack.push((startState, []))

    # while our nodeStack (Queue) isn't empty, we loop
    while not nodeStack.isEmpty():
        # currentNode and actions are popped off the stack
        currNode, actions = nodeStack.pop()
        # check if we're in a goal state, return the path if we are
        if problem.isGoalState(currNode):
            return actions
        # add currnode to explored set, continue if it's not there
        if currNode not in exploredSet:
            exploredSet.append(currNode)

            # now we can iterate through the successors of our current state
            for nextNode, action, cost in problem.getSuccessors(currNode):
                # from DFS pseudocode from class, if newnode isn't in explored set or already on stack
                if nextNode not in exploredSet and nextNode not in nodeStack.list:
                    # add the nextNode, and update the path 
                    nodeStack.push((nextNode, actions + [action]))

    # if we make it out of the while loop, can't find solution. 
    return []     

    

    """
    These were used to figure out how the program parts interract and are left for posterity
    print("Start:", problem.getStartState())
    print("Is the start a goal?", problem.isGoalState(problem.getStartState()))
    print("Start's successors:", problem.getSuccessors(problem.getStartState()))
    """
    

def breadthFirstSearch(problem):
    """Search the shallowest nodes in the search tree first."""
    "*** YOUR CODE HERE ***"
    # BFS is a first in first out algorithm, so we use a Queue
    nodeQueue = util.Queue()

    # need to maintain a list of explored nodes
    exploredSet = [] 

    # need our start state, and to check if it's already at the end, returning no actions
    startState = problem.getStartState()
    if problem.isGoalState(startState):
        return []

    # push the starting node and the (empty) list of actions onto the queue
    nodeQueue.push((startState, []))

    # loop while our queue isn't empty
    while not nodeQueue.isEmpty():
        # currentNode and actions are popped off the stack
        currNode, actions = nodeQueue.pop()
        # check if we're in a goal state, return the path if we are
        if problem.isGoalState(currNode):
            return actions
        # add currnode to explored set, continue if it's not there
        if currNode not in exploredSet:
            exploredSet.append(currNode)
            
            
            # now we can iterate through our successors and add them to the Queue
            for nextNode, action, cost in problem.getSuccessors(currNode):
                # from BFS pseudocode from class, if newnode isn't in explored set or already on stack
                if nextNode not in exploredSet and nextNode not in nodeQueue.list:
                    # add the next node, update the path 
                    nodeQueue.push((nextNode, actions + [action]))
    
    # if we make it out of the while loop, can't find a solution
    return []
                



def uniformCostSearch(problem):
    """Search the node of least total cost first."""
    "*** YOUR CODE HERE ***"
    # UCS is a priority queue based algorithm, so we'll use that
    nodePQueue = util.PriorityQueue()

    # explored set of nodes
    exploredSet = []

    # check our start state to see if we're at the end already
    startState = problem.getStartState()
    if problem.isGoalState(startState):
        return []
    
    # push the (starting node, actions, cost), and priority (0) onto the pQueue
    nodePQueue.push((startState, [], 0), 0)

    # loop while queue isn't empty
    while not nodePQueue.isEmpty():
        # should pop off the lowest cost item 
        currnode, actions, oldCost = nodePQueue.pop()

        # check if we're in a goalstate and return the actions if we are
        if problem.isGoalState(currnode):
            return actions

        # add currnode to the explored set if it's not there
        if currnode not in exploredSet:
            exploredSet.append(currnode)

            # iterate through successors and evaluate the costs
            for nextNode, action, cost in problem.getSuccessors(currnode):
                # cost equals previous node + new cost
                nextCost = oldCost + cost
                newNode = (nextNode, actions + [action], nextCost)
                # from algorithm pseudocode in class. Update if the cost to a node can be better. 
                if nextNode not in nodePQueue.heap:
                    nodePQueue.push(newNode, nextCost)
                elif nextNode in nodePQueue.heap and cost >= nextCost:
                    nodePQueue.update(newNode, nextCost)


def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    # it makes sense for A* to use a priority queue since it has cost
    nodePQueue = util.PriorityQueue()

    # explored set of nodes
    exploredSet = []

    # check our start state to see if we're at the end already
    startState = problem.getStartState()
    if problem.isGoalState(startState):
        return []

    # push the (starting node, actions, cost), and priority (0) onto the pQueue
    nodePQueue.push((startState, [], 0), 0)

    # loop while queue isn't empty 
    while not nodePQueue.isEmpty():
        # pop off the lowest cost item
        currnode, actions, oldCost = nodePQueue.pop()

        # if we're at a goal state return the actions to get there
        if problem.isGoalState(currnode):
            return actions

        # add currnode to the explored set if it's not already there
        if currnode not in exploredSet:
            exploredSet.append(currnode)

            # iterate through successors and evaluate the costs with heuristics
            for nextState, action, cost in problem.getSuccessors(currnode):
                # cost equals previous node + new cost
                nextCost = oldCost + cost
                nextNode = (nextState, actions + [action], nextCost)
                # these statements utilize the same logic as UCS but with the heuristic cost addition
                if nextState not in nodePQueue.heap:
                    nodePQueue.push(nextNode, nextCost + heuristic(nextState, problem))
                elif nextState in nodePQueue.heap and cost >= nextCost:
                    nodePQueue.update(nextNode, nextCost + heuristic(nextState, problem))

    


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch
