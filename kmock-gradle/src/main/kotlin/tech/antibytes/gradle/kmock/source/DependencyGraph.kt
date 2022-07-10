/*
 * Copyright (c) 2022 Matthias Geisler (bitPogo) / All rights reserved.
 *
 * Use of this source code is governed by Apache v2.0
 */

package tech.antibytes.gradle.kmock.source

import tech.antibytes.gradle.kmock.KMockPluginContract

internal object DependencyGraph : KMockPluginContract.DependencyGraph {
    private data class Node(val name: String, val children: Set<String>, val parents: Set<String>)

    private fun expand(
        dependencyGraph: Map<String, Node>,
        metaDependencies: Map<String, Set<String>>,
    ): Map<String, Node> {
        val traversed: MutableMap<String, Node> = dependencyGraph.toMutableMap()

        metaDependencies.forEach { (sourceSet, children) ->
            val currentNode = traversed[sourceSet]!!

            val childNodes = children.map { child ->
                val childNode = traversed[child]!!

                childNode.copy(
                    parents = childNode.parents.toMutableSet().also { it.add(currentNode.name) },
                ).also { node ->
                    traversed[child] = node
                }.name
            }

            traversed[sourceSet] = currentNode.copy(
                children = listOf(currentNode.children, childNodes).flatten().toSet(),
            )
        }

        return traversed
    }

    private fun Node.extractAncestors(dependencyGraph: Map<String, Node>): Set<String> {
        val ancestors: MutableSet<String> = mutableSetOf()

        this.parents.forEach { parent ->
            ancestors.add(parent)
            ancestors.addAll(dependencyGraph[parent]!!.extractAncestors(dependencyGraph))
        }

        return ancestors
    }

    private fun Map<String, Node>.extractAncestors(): Map<String, Set<String>> {
        return this.keys.associateWith { sourceSet -> this[sourceSet]!!.extractAncestors(this) }
    }

    override fun resolveAncestors(
        sourceDependencies: Map<String, Set<String>>,
        metaDependencies: Map<String, Set<String>>,
    ): Map<String, Set<String>> {
        val allSources: Map<String, Node> = listOf(sourceDependencies.keys, metaDependencies.keys)
            .flatten()
            .associateWith { sourceSet ->
                Node(
                    name = sourceSet,
                    children = emptySet(),
                    parents = emptySet(),
                )
            }.toMutableMap()

        return expand(allSources, metaDependencies).extractAncestors()
    }
}
