# Terminology
Just to prevent any confusion about terms let's make some quick definitions:

* A Proxy relates to a single property (PropertyProxy) or method (FunProxy).
* A Mock owns multiple Proxies based on an given Interface.
* Interfaces and their members are used as Templates for Proxies or a Mock.
* Assertions are strict about order which makes them fail in place and they are absolute/explicit about what they cover.
* Verifications fail after they traversed all available sources, which means they can skip elements.
