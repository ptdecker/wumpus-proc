# How to contribute

Third-party patches are essential for keeping any open source software great.
I want to keep it as easy as possible to contribute changes and improvements.
There are a few guidelines that I would like contributors to follow so that I
can have a chance of keeping on top of things.

## Getting Started

* Make sure you have a [GitHub account](https://github.com/signup/free)
* Submit a ticket for your issue, assuming one does not already exist.
  * Clearly describe the issue including steps to reproduce when it is a bug.
  * Make sure you fill in the earliest version that you know has the issue.
* Fork the repository on GitHub

## Making Changes

* Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Only target release branches if you are certain your fix must be on that
    branch.
  * To quickly create a topic branch based on master; `git branch
    fix/master/my_contribution master` then checkout the new branch with `git
    checkout fix/master/my_contribution`.  Please avoid working directly on the
    `master` branch.
* Make commits of logical units.
* Check for unnecessary whitespace with `git diff --check` before committing.
* Make sure your commit messages are in the proper format.

````
    ([issue number]) Make the example in CONTRIBUTING imperative and concrete

    Without this patch applied the example commit message in the CONTRIBUTING
    document is not a concrete example.  This is a problem because the
    contributor is left to imagine what the commit message should look like
    based on a description rather than an example.  This patch fixes the
    problem by making the example concrete and imperative.

    The first line is a real life imperative statement with a ticket number
    from our issue tracker.  The body describes the behavior without the patch,
    why this is a problem, and how the patch fixes the problem when applied.
````

* Make sure you have added the necessary tests for your changes.
* Run _all_ the tests to assure nothing else was accidentally broken.
* Obivously this trivial project currently has no tests. Test contributions would
certainly be most welcome.

## Making Trivial Changes

### Documentation

For changes of a trivial nature to comments and documentation, it is not
always necessary to create a new issue in GitHub. In this case, it is
appropriate to start the first line of a commit with '(doc)' instead of
a ticket number. 

````
    (doc) Add documentation commit example to CONTRIBUTING

    There is no example for contributing a documentation commit
    to the Puppet repository. This is a problem because the contributor
    is left to assume how a commit of this nature may appear.

    The first line is a real life imperative statement with '(doc)' in
    place of what would have been the ticket number in a 
    non-documentation related commit. The body describes the nature of
    the new documentation or comments added.
````

## Submitting Changes

* Push your changes to a topic branch in your fork of the repository.
* Submit a pull request to the repository in the puppetlabs organization.
* Add a comment to your issue to  indicate that you have submitted code and are ready for it to be reviewed. Please include a link to the pull request in the ticket.
* After feedback has been given please respond within two weeks. After two weeks I may close the pull request if it isn't showing any activity.
* Consider updating the wiki if appropriate with any technical information related to your submission.

# Additional Resources

* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)

# Credits

This CONTRIBUTE.md file is based upon one found at [Puppet Labs] (https://raw.githubusercontent.com/puppetlabs/puppet/master/CONTRIBUTING.md).