Implementing bloom filter with bit slicing array to store the index.
The number of hashfunctions (k) for a given term is decided based on the frequency and Signal-to-noise ratio

bloom filter index

term_id [1 0 1 0 0]
where activated bits combined at different locations indicates whether given term is present in the document.

Since bloom filter uses compact space , the activated bit can represent the value for multiple documents.

But instead of using bit to present the document location; multiple bits are activated by different hash function. If all of them are 1, indicates the terms is present in the document.

The algorithm definitely finds out if something is not present, but may lead to false positive in case if the bit is activated by some other term.

for example
0 0 0 are three position representing a term present in 8 documents

q1 in d1 turned bits 1 0 1 q1 in d2 might not be there but may turn same bits q1 in d2: hash functions generates same bit 1 0 1 q1 in d2 is false positive

the bit density and signal to noise ratio is used to control false positive rate.

To calculate number of hash functions need to be used to find bits for given term

ki = min(3;logd(ai/((1-ai)*snr)))

ai - is term frequency snr - signal to noise ratio logd - d is the bit density

Result Files generated While Indexing:
bloomFilter.txt – the actual bit sliced array stored in a file bloomFilter_shape.txt – dimensions of bitSliced array. term_hash_lookup – term to number of hash functions generated. document_lookup.txt – lookup file for document names. expectedResult.txt – the sparse matrix for terms.
