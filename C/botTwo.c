int MinMax(int depth) 
{ 
    if (SideToMove() == WHITE)    // White is the "maximizing" player. 
        return Max(depth); 
    else                          // Black is the "minimizing" player. 
        return Min(depth); 
} 
  
int Max(int depth) 
{ 
    int best = -INFINITY; 
  
    if (depth <= 0) 
        return Evaluate(); 
    GenerateLegalMoves(); 
    while (MovesLeft()) { 
        MakeNextMove(); 
        val = Min(depth - 1); 
        UnmakeMove(); 
        if (val > best) 
            best = val;
    } 
    return best; 
} 
  
int Min(int depth) 
{ 
    int best = INFINITY;  // <-- Note that this is different than in "Max". 
  
    if (depth <= 0) 
        return Evaluate(); 
    GenerateLegalMoves(); 
    while (MovesLeft()){ 
        MakeNextMove(); 
        val = Max(depth-1); 
        UnmakeMove(); 
        if (val < best)  // <-- Note that this is different than in "Max". 
            best = val;
    } 
    return best; 
}