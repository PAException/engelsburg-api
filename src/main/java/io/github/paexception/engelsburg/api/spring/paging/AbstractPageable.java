package io.github.paexception.engelsburg.api.spring.paging;

import org.springframework.data.domain.PageRequest;

/**
 * Use for controller classes which can support paging.
 */
public abstract class AbstractPageable {

	private final int minSize;
	private final int maxSize;

	/**
	 * Initialize boundaries.
	 *
	 * @param minSize min amount of entities by request.
	 * @param maxSize max amount of entities by request.
	 */
	public AbstractPageable(int minSize, int maxSize) {
		if (minSize < 0 || maxSize < 0)
			throw new IllegalArgumentException("Maximum and minimum cannot be less than zero");
		if (minSize > maxSize) throw new IllegalArgumentException("Maximum size cannot be less than minimum size");

		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	protected PageRequest toPage(Paging paging) {
		return this.toPage(paging.getPage(), paging.getSize());
	}

	/**
	 * Assemble PageRequest by given page and size.
	 *
	 * @param page for paging
	 * @param size for paging
	 * @return assembled PageRequest
	 */
	protected PageRequest toPage(int page, int size) {
		if (page < 0) page = 0;
		if (size > this.maxSize || size == 0) size = this.maxSize;
		if (size < this.minSize) size = this.minSize;

		return PageRequest.of(page, size);
	}

}
