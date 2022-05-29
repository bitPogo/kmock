# Terminology
Just to prevent any confusion about terms let's make some quick definitions:
* A Proxy relates to a single property (PropertyProxy) or method (FunProxy).
* A Mock owns multiple Proxies based on an given Interface.
* Interfaces and their members are used as Templates for Proxies or a Mock.
* Assertion fails in place, which means it will be strict about order.
* Verification fails after it traversed all available sources, which means it can skip elements.
