let s:source = {
\   'name': 'omnijava',
\   'kind': 'manual',
\   'filetypes': { 'java': 1 },
\   'min_pattern_length': g:neocomplete#auto_completion_start_length,
\   'hooks': {},
\ }

function! s:source.hooks.on_init(context) dict
endfunction

function! s:source.hooks.on_final(context) dict
endfunction

function! s:source.get_complete_position(context) dict
  return omnijava#get_keyword_col(a:context.input)
endfunction

function! s:source.gather_candidates(context) dict
  return omnijava#get_complete_words(a:context.input, a:context.complete_str)
endfunction

function! neocomplete#sources#omnijava#define()
  return s:source
endfunction
