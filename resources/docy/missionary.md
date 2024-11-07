

Asynchronous operators (i.e. ? and ?>) are not allowed in cp. Therefore, cp flows are always initialized, so they can be passed to latest or signal without a reductions stage. cp also leverages the non-asynchronous property to defer computation on consumer demand, whereas ap computes ASAP.
My current understanding is that ap and cp are actually the same thing, cp is just the special case of ap with no asynchronous operators and the continuous time semantics can be inferred from the static structure of the code, more details here https://github.com/leonoel/missionary/issues/109

