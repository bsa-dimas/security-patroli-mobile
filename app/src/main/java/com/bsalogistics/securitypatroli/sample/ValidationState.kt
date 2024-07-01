package com.bsalogistics.securitypatroli.sample

sealed class ValidationState {
    object Loading : ValidationState()
    data class Success(val sampleModel: SampleModel) : ValidationState()
}