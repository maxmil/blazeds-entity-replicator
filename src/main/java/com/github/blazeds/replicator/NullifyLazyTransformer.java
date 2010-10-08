/**
 * 
 */
package com.github.blazeds.replicator;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

import org.hibernate.Hibernate;

/**
 * @author Max Pimm
 * 
 *         Custom transformer used by beanlib to nullify hibernate collections
 *         that have not been initialized
 * 
 */
public final class NullifyLazyTransformer implements CustomBeanTransformerSpi {

	public static class Factory implements CustomBeanTransformerSpi.Factory {
		public CustomBeanTransformerSpi newCustomBeanTransformer(
				BeanTransformerSpi beanTransformer) {
			return new NullifyLazyTransformer(beanTransformer);
		}
	}

	private NullifyLazyTransformer(BeanTransformerSpi beanTransformer) {
	}

	@Override
	public boolean isTransformable(Object from, Class<?> toClass,
			PropertyInfo propertyInfo) {
		return !Hibernate.isInitialized(from);
	}

	@Override
	public <T> T transform(Object in, Class<T> toClass,
			PropertyInfo propertyInfo) {
		return null;
	}

}
