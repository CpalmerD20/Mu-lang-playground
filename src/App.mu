print "Variable Assignment Test";
let id = "Steve 7";
print id;
id = "Lucy 7";
print id;

print "Immutable assignment Test";
# thisNumber = 22;
print "One day this syntax will create immutable values.";

print "Closure Test";

define addTogether(x, y) { print x; return x + y; }
print addTogether(3, 5);

define makeCounter() {
   let i = 0;
   define count() {
       i = i + 1;
       print i;
   }
   return count;
}

# counter = makeCounter();
counter();
counter();

print "Control Flow Test: if {} else {}";
if false {
   print 0;
} else {
   print 22;
}

if true {
   print 0;
} else print 22;

if false {
   print 0;
} else if false {
   print 22;
} else print "dave";

print "Control Flow Test: repeat statement";
repeat {
   print "dave ";
   until true;
   print "shouldn't get here";
}
repeat {
   print "dave ";
   print "should get here";
   until true;
}

print "Count to 10 please.";
let i = 0;
repeat {
   i = i + 1;
   print i;
   until i >= 10;
}

print "Can you do it again?";
define countTo(x) {
   let i = 0;
   repeat {
       until i >= x;
       i = i + 1;
       print i;
   }
}

countTo(10);

print "String Concatenation";

# newString = join "Mary " "had " "a " "little " "lamb.";
print newString;

# newerString = join { "one" ", " "two" ", " "three" "!" };
print newerString;

*********************** Planned Syntax ***********************

// anonymous lambda functions

# addTogether = each (x, y) return x + y;

# addTwoSquares = each (x, y) {
   # squareX = x ^* 2;
   # squareY = x ^* 2;
   return squareX + squareY;
}

addTogether(4, 6);    // 10
addTwoSquares(3, 4);  // 25
