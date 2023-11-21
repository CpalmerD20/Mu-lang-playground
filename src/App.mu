
print "Variable Assignment Test";
let id = "Steve 7";
print id;
id = "Lucy 7";
print id;

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
    until i > 10;
}

print "Can you do it again?";
define countTo(x) {
    let i = 0;
    repeat {
        until i > x;
        i = i + 1;
        print i;
    }
}

countTo(10);