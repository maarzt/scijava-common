
package org.scijava.log;

import org.scijava.Priority;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * This {@link PreprocessorPlugin} affects {@link Module}s with a single
 * {@link Parameter} of type {@link Logger}. It will assign a Logger to that
 * Parameter, that is named like the modules class.
 *
 * @author Matthias Arzt
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.NORMAL_PRIORITY)
public class LoggerPreprocessor extends AbstractPreprocessorPlugin {

	@Parameter(required = false)
	private LogService logService;

	@Parameter(required = false)
	private ModuleService moduleService;

	// -- ModuleProcessor methods --

	@Override
	public void process(final Module module) {
		if (logService == null || moduleService == null) return;

		final ModuleItem<?> loggerInput = moduleService.getSingleInput(module,
			Logger.class);
		if (loggerInput == null || !loggerInput.isAutoFill()) return;

		final String name = loggerInput.getName();
		module.setInput(name, logService.subLogger(module.getDelegateObject()
			.getClass().getSimpleName()));
		module.resolveInput(name);
	}

}
