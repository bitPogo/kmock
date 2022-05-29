# Setup
## Mandatory Options
KMock brings a variety of configuration options.
In order to access KMock's extension in Gradle please do the following **after** it's is apply:

=== "Android/JVM"
    ```kotlin
    plugins {
        ...

        id("tech.antibytes.kmock.kmock-gradle")
    }

    kmock {
        rootPackage = "my.root.package"
    }
    ```
=== "Kotlin Multiplatform/KJs"
    ```kotlin
    import tech.antibytes.gradle.kmock.KMockExtension
    ...

    plugins.apply("tech.antibytes.kmock.kmock-gradle")

    project.extensions.configure<KMockExtension>(KMockExtension::class.java) {
        rootPackage = "tech.antibytes.kmock.example"
    }
    ```

The only **mandatory option** is `rootPackage`, which expects the root package of **your project** as a String.
This information is need in order to arrange the generate mocks properly.

## Optional Features
### Aliases
In order to resolve conflicts of mock names KMock allows you make mapping of aliases via `aliasNameMapping`.
It expects a the key the full qualified name of the template target and as value the alias as a short name:
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
KMock will not generate proxies for build-in methods like hashCode by default in order to avoid unnecessary overhead in terms of the compile time.
However if you need build-in methods you can tell KMock to generate via `useBuildInProxiesOn`.
It expects a set of full qualified name of target templates which should use proxy build-in methods:
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
If KMock produces a collision of methods while resolving names for overloaded methods you can help it via `useTypePrefixFor`.
It expects a map, while the key  must be the full qualified name of causing type and the value is an arbitrary String, which is used as a prefix.
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
In order to spy based on a certain template KMock offers you the `spyOn`.
It expects a set of full qualified names of templates which are allowing spies.
!!!note
    This will enable build-in methods for this particular mock as well.
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
By default KMock will only activate spies via `spyOn`, due to the overhead in terms of compile time.
If you simply want to enable for all templates spies you can do that via `spyAll`:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    spyAll = true
}
```

### Spies only
If you have no use for mocks which cannot be spied and you want to spare the compile time of the `kmock` factories,
you tell KMock this via `spiesOnly`:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    spiesOnly = true
}
```

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
By the default all mocks are frozen in order to avoid any issues which can occur with Coroutines.
If you do not wish that default you can set a new one via `freezeOnDefault`:
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    freezeOnDefault = false
}
```

### Disable Factories
One of the biggest factors in terms of compile time are the generated factories.
If you do not mind to wire everything by hand you can disable them via `disableFactories`:
!!!warning
    If you do that you are on your own.
    Also constructors of mocks are considered as internal.
    This means do not expect them to have a stable api.
```kotlin
kmock {
    ...
    rootPackage = "my.root.package"
    disableFactories = true
}
```

### Custom Annotations for Shared Sources
In order to ease the declaration of Mocks for Shared Sources (e.g. `nativeTest`) offers you a hook to define custom
annotations via `customAnnotationsForMeta` which takes a map.
It expects as key the full qualified name of your Annotation and as value the (valid) source set it is referring too.

!!!note
    Before you ask - no, inheritance does not work with annotations.

!!!important
    `commonTest` as source set is not allowed!

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
It expects a map while the key must be the full id of the Proxy you are refering to and the value can be an arbitrary String, which is used as name for the Proxy.

!!!warning
    This feature will hurt you if the target template is subject to changes, since you have to adjust the mapping each time.
```
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
```
kmock {
    ...
    rootPackage = "my.root.package"
    allowExperimentalProxyAccess = true
}
```

### Fine Grained ProxyNames
If you have the need of Proxy names which support the full extend of KMP deferring types you can enable `enableFineGrainedNames`.
!!!note
    You will not need that if you work only with JVM or Android.
    This flag will you only help with native sources and JS to certain degree.
```
kmock {
    ...
    rootPackage = "my.root.package"
    enableFineGrainedNames = true
}
```
