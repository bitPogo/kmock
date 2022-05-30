# Migration Guide
Apologies! Some was broken to improve KMock.
But don't panic!

## 0.1.1
### API changes
#### Names

* FunProxy names with nullable or Multi-Bounded types defined as generic parameter - this may break some Proxies if they are referring to overloaded methods.
* Multi-Bounded Generics caused invalid Proxy name if overloaded and a leading boundary was nullable - this may break some Proxies if they are referring to overloaded methods.
* The `verifier` argument of factories is now called `collector`.
* `NonfreezingVerifier` is now `NonFreezingVerifier`.
* `verifyStrictOrder` is now `assertOrder`.

#### Removed

* `allowedRecursiveTypes` - this option is obsolete and would have no effect.
* `allowInterfacesOnKmock` - this option is obsolete and would lead to undesired behaviour, but you can use `allowInterfaces` instead.
* `allowInterfacesOnKspy` - this option is obsolete and would lead to undesired behaviour, but you can use `allowInterfaces` instead.
* The experimental ProxyAssertion family - those methods were only aliases for `verify(exacty = 1) { ... }`. Please consider `assertProxy` or `verify` instead.
* `uselessPrefixes` in the Gradle Extension - this option is obsolete and would have no effect.

#### Changed

* Expectation Methods do not bleed into the global context any longer. This means the imports of the old extensions must be removed and it they will work again.

### Behaviour Changes

* `verifyStrictOrder` is now used for total order of certain Proxies but allows partial order between different Proxies, use `assertOrder` instead.
