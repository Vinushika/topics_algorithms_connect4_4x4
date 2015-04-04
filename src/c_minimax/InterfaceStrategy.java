package c_minimax;

//author: Gary Kalmanovich; rights reserved

public interface InterfaceStrategy {
    void getBestMove(InterfacePosition position, InterfaceSearchInfo context); // Return info is in context
    void setContext( InterfaceSearchInfo strategyContext );
    InterfaceSearchInfo getContext();
}
