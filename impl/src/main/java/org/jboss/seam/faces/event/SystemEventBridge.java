/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.faces.event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.util.AnnotationLiteral;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import org.jboss.seam.faces.cdi.BeanManagerAware;
import org.jboss.seam.faces.event.qualifier.Component;

/**
 * A SystemEventListener used to bridge JSF system events to the CDI event
 * model.
 * <p>
 * 
 * For each JSF system event (e.g: {@link PostConstructApplicationEvent}, a
 * corresponding Seam CDI event will be fired.
 * <p>
 * 
 * Event listeners can be registered by observing the appropriate Seam CDI event
 * (see @{@link Observes}):
 * <p>
 * <b>For example:</b>
 * <p>
 * <code>
 * public void listener(@Observes org.jboss.seam.faces.event.qualifier.ExceptionQueuedEvent event)
 * {
 *    //do something
 * }
 * </code>
 * 
 * @author Nicklas Karlsson
 */
@ApplicationScoped
public class SystemEventBridge extends BeanManagerAware implements SystemEventListener
{

   public boolean isListenerForSource(final Object source)
   {
      return true;
   }

   public void processEvent(final SystemEvent e) throws AbortProcessingException
   {
      Object payload = e.getClass().cast(e);
      if (e instanceof ComponentSystemEvent)
      {
         ComponentSystemEvent ce = (ComponentSystemEvent) e;
         String id = ce.getComponent().getId();
         getBeanManager().fireEvent(e, new ComponentLiteral(id));
      }
      else
      {
         getBeanManager().fireEvent(payload);
      }
   }

   private class ComponentLiteral extends AnnotationLiteral<Component> implements Component
   {
      private final String value;

      public String value()
      {
         return value;
      }

      public ComponentLiteral(String value)
      {
         this.value = value;
      }
   }   

}