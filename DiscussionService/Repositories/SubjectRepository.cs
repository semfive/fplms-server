using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;

namespace DiscussionService.Repositories
{
    public class SubjectRepository : RepositoryBase<Subject>, ISubjectRepository
    {
        public SubjectRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }
    }
}