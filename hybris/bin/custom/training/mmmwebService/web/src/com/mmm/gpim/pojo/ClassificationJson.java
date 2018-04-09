/**
 *
 */
package com.mmm.gpim.pojo;



/**
 * @author rjo3959
 *
 */
public class ClassificationJson
{

	private String targetClassficationClass;

	/**
	 * @return the targetClassficationClass
	 */
	public String getTargetClassficationClass()
	{
		return targetClassficationClass;
	}

	/**
	 * @param targetClassficationClass
	 *           the targetClassficationClass to set
	 */
	public void setTargetClassficationClass(final String targetClassficationClass)
	{
		this.targetClassficationClass = targetClassficationClass;
	}

	/**
	 * @return the sourceclassificationClass
	 */
	public String getSourceclassificationClass()
	{
		return sourceclassificationClass;
	}

	/**
	 * @param sourceclassificationClass
	 *           the sourceclassificationClass to set
	 */
	public void setSourceclassificationClass(final String sourceclassificationClass)
	{
		this.sourceclassificationClass = sourceclassificationClass;
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute()
	{
		return attribute;
	}

	/**
	 * @param attribute
	 *           the attribute to set
	 */
	public void setAttribute(final String attribute)
	{
		this.attribute = attribute;
	}


	private String sourceclassificationClass;


	private String attribute;
}
