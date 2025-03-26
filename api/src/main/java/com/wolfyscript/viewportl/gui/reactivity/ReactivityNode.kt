package com.wolfyscript.viewportl.gui.reactivity

interface ReactivityNode {

    val state: State

    /**
     * Notifies the subscribers (if there are any), that this nodes value has changed.
     */
    fun notifySubscribers()

    /**
     * Updates the Node when it is necessary.
     * An update is necessary when any source of the specified node is [ReactivityNode.State.DIRTY].
     *
     * The [ReactivityNode.State.DIRTY] state propagates through the Graph from top to bottom.
     * Therefor this method goes up the Graph, starting at this Node, checking recursively if any sources are [ReactivityNode.State.DIRTY].
     *
     * Note that a [ReactivityNode.State.DIRTY] state can stop propagating down. For Example when a Memo doesn't change its value.
     *
     * @return If the value of the node has changed
     */
    fun updateIfNecessary(): Boolean

    /**
     * A depth-first DAG (Direct Acyclic Graph) with its origin at this node.
     * It recursively marks all child nodes as [ReactivityNode.State.CHECK].
     */
    fun markDirty()

    /**
     * Marks this node as [State.CHECK].
     * Not all nodes have a [State.CHECK] state, so it depends on the specific implementation.
     */
    fun markCheck()

    /**
     * The state of a [ReactivityNode]
     */
    enum class State {
        /**
         * Node is clean, it does not need to be updated
         */
        CLEAN,

        /**
         * Node may have changed, needs to be checked
         */
        CHECK,

        /**
         * Node has changed, needs to update
         */
        DIRTY,
    }
}