# Setup
## Mandatory Options
KMock brings a variety of configuration options.
In order to access KMock's extension in Gradle please do the following:


```kotlin
    plugins {
        ...

        id("tech.antibytes.kmock.kmock-gradle")
    }

    kotlin {
        ...
    }

    kmock {
        rootPackage = "my.root.package"
    }
```

The only **mandatory option** is `rootPackage`,
It expects the root package of **your project** as a String.
This information is need in order to arrange the generate Mocks properly.

## Optional Features
### Aliases
In order to resolve conflicts of mock names KMock allows you make mapping of aliases via `aliasNameMapping`.
It expects a the key of the full qualified name of the Template and as value the Alias as a short name:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    aliasNameMapping = mapOf(
        "my.root.package.template.alias.Collision" to "Alias",
    )
}
```

### Build-In Methods
KMock will not generate proxies for Build-In methods like hashCode by default in order to avoid unnecessary overhead.
However if you need Build-In methods you can tell KMock to generate them via `useBuildInProxiesOn`.
It expects a set of full qualified names of Templates which should have proxies for Build-In methods:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    useBuildInProxiesOn = setOf(
        "my.root.package.template.alias.BuildInMethods"
    )
}
```

### Colliding Method Names
If KMock produces a collision of names for overloaded methods you can resolve it via `useTypePrefixFor`.
It expects a map, while the key must be the full qualified name of causing type and the value is an arbitrary String, which is used as a prefix.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    useTypePrefixFor = mapOf(
        "my.root.package.types.overloaded.Scope.Abc" to "Prefix"
    )
}
```

### Spies
In order to enable Spies for certain Templates, KMock offers you the `spyOn`.
It expects a set of full qualified names of Templates.
!!!note
    This will enable Build-In methods for this particular mock as well.
!!!note
    Spies for Multi-Mocks will triggered if one Interface can be spied on.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    spyOn = setOf(
        "my.root.package.templates.spy.SpyOnMe"
    )
}
```

### Spy on All
By default KMock will only activate spies via `spyOn`.
If you simply want to enable spying for all Templates spies you can do that via `spyAll`:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    spyAll = true
}
```

### Spies only
If you have no use for Mocks which cannot be spied and you want to spare the compile time of the `kmock` factories,
you may tell this KMock via `spiesOnly`:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    spiesOnly = true
}
```
!!!note
    `spiesOnly` will trigger that all Mocks can are spied on like `spyAll`.

### Enable Interface References
In order to be able to utilize interfaces for the `kmock` or `kspy` factory you can enable this via `allowInterfaces`:
!!!tip
    This will be handy if you use it together with Relaxation.
!!!note
    This will not work for Multi Interface Mocks.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    allowInterfaces = true
}
```

### Default Freezing behaviour
By the default all Mocks will not be frozen. If you do not wish that default you can set a new one via `freezeOnDefault`:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    freezeOnDefault = true
}
```

### Disable Factories
One of the biggest factors in terms of compile time are the generated factories.
If you do not mind to wire everything by hand you can disable them via `disableFactories`:
!!!warning
    If you do that you are on your own.
    Also constructors of Mocks are considered as internal.
    This means do not expect them to have a stable API.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    disableFactories = true
}
```

### Custom Annotations for Shared Sources
In order to ease the declaration of Mocks for Shared Sources (e.g. `nativeTest`)  KMock offers you a hook to define custom
annotations via `customAnnotationsForMeta`.
It takes a map.
The key must be the full qualified name of your Annotation and the value a (valid) Source Set your Annotations is referring to.

!!!note
    Before you ask - no, inheritance does not work with annotations.

!!!important
    `commonTest` as Source Set is not allowed!

```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    customAnnotationsForMeta = mapOf(
        "my.root.package.annotation.Native" to "nativeTest"
    )
}
```

## Experimental Features
The following features are considered as experimental, which means there likely be to changed or removed.

### CustomMethodNames
Since overloaded method names of Proxies can become very ugly KMock offers you the possibility to rename them via `customMethodNames`.
It expects a map while the key must be the full id of the Proxy you are referring to and the value can be an arbitrary String, which is used as name for the Proxy.

!!!warning
    This feature will hurt you if the Template is subject to changes, since you have to adjust the mapping each time.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    customAnnotationsForMeta = mapOf(
        "my.root.package.template.renamed.OverloadedMock#_foo" to "bar"
    )
}
```

### Proxy Access Methods
In order to enable the experimental ProxyAccessMethods you can switch `allowExperimentalProxyAccess` to true:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    allowExperimentalProxyAccess = true
}
```

### Fine Grained ProxyNames
If you have the need of Proxy names which support the full extend of KMP deferring types you can enable `enableFineGrainedNames`.
!!!note
    You will not need that if you work with JVM or Android.
    This flag will you only help withs native sources and JS.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    enableFineGrainedNames = true
}
```

### Prevent resolving of Aliases
It may occur that AccessMethods in combination with expect/actual Aliases cause problems with incremental builds.
Since it is currently not possible to resolve that automatically you need to declare them by hand:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    preventResolvingOfAliases = "my.root.package.subpacke.Alias"
}
```
