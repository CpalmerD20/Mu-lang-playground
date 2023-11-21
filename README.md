# Mu Lang Playground

This is an implementation of transpiler to experiment with potential syntax and grammars for Mu lang. To revisit what it means to be a more human readable programming language. Languages like Python and Ruby were developed with this focus in the 90s and the programming landscape has changed a lot since then. [Sample code snippets can be seen here](src/App.mu)


This implementation uses Jlox as a guide for a working interpreter. (1) I wanted to have a project that had recursion as a use case and (2) having Jlox to refer to would hopefully make the project quicker to implement.

## Mu Goals
To use whole words with clear meaning.
To be easier for newer developers to learn.
To have consistent syntax that is easy to visually parse.
The language prioritizes ease of learning and ergonomics.

## Mu Principles (Far Future Implementation Goals)
A procedural language that supports first class functions.
Support for objects without inheritance (traits and composition instead).
Support for immutable variables and data structures, as well as, atomic variables and data structures.

### Whole Words with Clear Meaning

While it is tempting to commit to common keywords like ‘var’ or ‘const’ I believe this can give developers the impression that terse naming conventions are the way to go. If a language is to promote the developer writing clear names, then the language itself should promote this as well.

This is not to say that this language will be free of operators. Operators are fantastic when they provide clear meaning (2 + 2) or (2 / 2) are very clear in what they express. Some operators will be questioned about how they could be better represented. Overall I think it’s important to also have relative consistency with other languages to help programmers who use multiple languages.

To discuss this further while loops are a common pattern, but they seem to be out of fashion as we have thought of different ways to write iterations and loops. Below are a do-while and while example, which are consistently referenced as difficult to parse.
```
var sum = 0; 
do {
	const value = list.pop();
	sum += value;

} while (list.length > 0);

var sum = 0;
while (queue.length > 0) {
	const value = queue.remove();

	if (value < 1) {
		continue;
	}
	sum += value;
}  
```
For me the problem is the words “while”, “continue”, and “do” are not fully clear. We want to repeat procedures until a condition is satisfied and sometimes we always want procedures to occur at least once. Wouldn’t it be better to “repeat” “until”? It’s also more clear on what the procedure is doing if the keyword is “skip” instead of “continue”.
```
let sum = 0;
repeat {
	# value = list.pop();  
	sum += value;
	until list.size() < 1;
}

let sum = 0; 
repeat {
	until queue.size() < 1;
	# value = queue.remove();

	if value < 1 {
		skip;
	}
	sum += value;
}
```
Compared to do-while it’s more explicit why some procedures will only occur once. With a term like “skip” it’s more clear that sum += value might be skipped over. Overall these keywords would be much easier to explain to new developers.

## Remaining Sections Under Construction
