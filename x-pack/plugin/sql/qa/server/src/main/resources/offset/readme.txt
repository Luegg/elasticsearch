Queries with OFFSET require a fetch_size of at least LIMIT but the normal SQL specs use a random fetch_size that can
be as low as 1. By putting these tests into a separate folder they won't be picked up by the normal specs but only
by the SqlOffsetSpecs.
