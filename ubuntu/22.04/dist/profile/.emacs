
(setq user-mail-address "luwrain-user@mail.ru")
(setq user-full-name "The LUWRAIN user")

(global-set-key [f2] 'save-buffer)
(global-set-key [f3] 'find-file)
(global-set-key [f4] 'kill-buffer)
(global-set-key [f5] 'other-window)
(global-set-key [f6] 'calendar)
(global-set-key [f7] 'gnus)
(global-set-key [f8] 'w3m-browse-url)
(global-set-key [f9] 'compile)
(global-set-key [f10] 'switch-to-buffer)
(global-set-key [f11] 'delete-other-windows)
(global-set-key [S-f3] 'shell-command)
(global-set-key [S-f4] 'shell)

(global-set-key (kbd "M-а") 'forward-word)
(global-set-key (kbd "M-и") 'backward-word)
(global-set-key (kbd "C-ц") 'kill-region)
(global-set-key (kbd "M-ц") 'kill-ring-save)
(global-set-key (kbd "C-н") 'yank)

;;Open shell in the current window
(push (cons "\\*shell\\*" display-buffer--same-window-action) display-buffer-alist)

(setq make-backup-files nil)
(setq inhibit-startup-screen t)
(setq line-move-visual nil)
(setq emacspeak-gnus-punctuation-mode 'all)
(setq emacspeak-message-punctuation-mode 'all)
(setq dtk-unicode-untouched-charsets '(ascii cyrillic-iso8859-5 latin-iso8859-1))
(setq kill-buffer-query-functions nil)
(setq emacspeak-play-emacspeak-startup-icon nil)

(require 'message)
(setq mm-coding-system-priorities '(koi8-r))
(add-to-list 'mm-body-charset-encoding-alist '(koi8-r . 8bit))
(setq message-cite-function 'message-cite-original-without-signature)
(setq smtpmail-default-smtp-server "localhost")
(setq send-mail-function 'smtpmail-send-it)
(require 'smtpmail)

(require 'w3m)
(setq mm-text-html-renderer 'w3m-standalone)
(remove-hook 'w3m-after-cursor-move-hook 'w3m-print-this-url)
(remove-hook 'w3m-after-cursor-move-hook 'w3m-show-form-hint)

(remove-hook 'w3m-after-cursor-move-hook 'w3m-highlight-current-anchor)
(remove-hook 'w3m-after-cursor-move-hook 'w3m-auto-show)

(setq browse-url-browser-function 'w3m-browse-url)
(setq w3m-search-engine-alist
      '(
        ("google" "http://www.google.ru/search?q=%s&hl=ru&ie=koi8-r" koi8-r)
        ))
(setq w3m-search-default-engine "google")
(setq w3m-use-cookies t)

(require 'dired-x)
(add-hook 'dired-mode-hook
	  (lambda ()
	    (setq dired-omit-files-p t)
	    (setq dired-omit-extensions '("~" ".o" ".a" ".la" ".toc" ".aux" ".log"))
	    (setq dired-omit-files "^\\.")
	    ))

;;java
(require 'cc-mode)
(add-hook 'java-mode-hook (lambda () 
			    (setq c-basic-offset 4)
			    (c-set-offset  'substatement-open 0)
			    (c-set-offset  'statement-cont 0)
			    ))

(defun emacspeak-speak-shell-command-output (&optional output)
  (emacspeak-auditory-icon 'task-done)
  (cond
   ((or (stringp output) (bufferp output)) nil)
   ((not output)
    (let ((buffer (get-buffer "*Shell Command Output*")))
      (when buffer
        (with-current-buffer buffer
          (if (= (point-min) (point-max))
              (dtk-speak (current-message))
            (emacspeak-speak-region (point-min) (min (point-max) 8192))
            (when (> (point-max) 8192)
              (emacspeak-queue-auditory-icon 'ellipses)))))))
   (:else
    (if (= (point) (mark 'force))
        (dtk-speak "Empty output")
      (emacspeak-speak-region (point) (min (mark 'force) (+ (point) 8192)))
      (when (> (mark 'force) (+ (point) 8192))
        (emacspeak-queue-auditory-icon 'ellipses))))))

(defadvice shell-command (around emacspeak activate)
  (let ((emacspeak-speak-messages nil))
    ad-do-it)
  (when (called-interactively-p 'interactive)
    (emacspeak-speak-shell-command-output (ad-get-arg 1))))

(defadvice dired-do-shell-command (around emacspeak activate)
  (let ((emacspeak-speak-messages nil))
    ad-do-it)
  (when (called-interactively-p 'interactive)
    (emacspeak-speak-shell-command-output)))


