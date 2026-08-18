package io.github.st2client.resource;

import io.github.st2client.model.Resource;

import java.util.List;

/**
 * Query result with total count from the {@code X-Total-Count} header.
 *
 * @param <T> the resource type
 * @param items the list of resource items in the current page
 * @param totalCount total number of resources matching the query
 * @author crudb0y69
 * @since 0.1.0
 */
public record QueryResult<T extends Resource>(List<T> items, int totalCount) {}
