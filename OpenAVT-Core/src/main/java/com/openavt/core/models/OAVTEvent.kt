package com.openavt.core.models

/**
 * An OpenAVT Event.
 *
 * @param action Action.
 */
open class OAVTEvent(action: OAVTAction): OAVTSample() {

    /**
     * Get event action.
     */
    val action = action

    /**
     * Get event attributes.
     */
    val attributes: MutableMap<OAVTAttribute, Any> = mutableMapOf()

    /**
     * Get normalized dictionary of attributes.
     *
     * @return Attributes.
     */
    fun getDictionary(): Map<String, Any> {
        val dic: MutableMap<String, Any> = mutableMapOf()
        for ((attr, value) in this.attributes) {
            dic[attr.attributeName] = value
        }
        return dic
    }

    /**
     * Convert object to string.
     *
     * @return Object representation.
     */
    override fun toString(): String {
        return "Action: " + action + " , Attributes: " + attributes
    }
}