# expression-evaluator
A micro-service to enable on the fly expression evaluation.

# motivation
ETSI has specified the guidelines for virtual network function (VNF) descriptors and network service (NS) descriptors. These descriptors can be extended to contain assurance expressions and monitoring paramaters for constitutent VNFs of the NS. For NS orchestration, especially if the orchestrator design is influenced by micro-services design pattern, a small service that quickly evaluates these ETSI compliant VNF assurance expressions can be handy.

# supported expressions
This micro-service provides a REST API for on-the-fly expression evaluation, the expression must be a well formed functional composition. You can think of it as synonimous to a prefix expression. Parameters and constants are allowed, and the parameters values to be used in the expression must be supplied with the API call for evaluation.

Any well formed composed function expression is supported (to any depth)- 

```fn-x(p1, p2, fn-y(q1, q2, fn-z(r1, r2, r3)))```

The parameter list must be passed in the order they appear in the expression as a JSON array.

## example expressions
### vnf availability
GT(min(vnfs[1].availability, vnfs[2].availability))
### ns ram consumption
LT(add(vnfs[1].memory-consumption, vnfs[2].memory-consumption, 100))

# current functions support

1. ```add()``` - sum of parameters
2. ```min()``` - minimum value selection from the argument list
3. ```max()``` - maximum value selection from the argument list
4. ```avg()``` - average of the parameters
5. ```lt()``` - true if the argument is less than a threshold - threshold value is provided separately
6. ```gt()``` - true if the argument is greater than a threshold - threshold value is provided sepaately

# installation requirements
* Java runtime environemt (7.0 or higher)
* maven 3.0 or higher

# one-step installation steps (ubuntu or debian system)
* download the setup script - https://raw.githubusercontent.com/piyush82/expression-evaluator/master/setup.sh
* make the script executable - ```chmod +x setup.sh```
* execute the script - ```./setup.sh```
* follow the prompts and you are done.